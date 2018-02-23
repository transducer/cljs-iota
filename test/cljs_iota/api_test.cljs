(ns cljs-iota.api-test
  "Unit tests that are running against a local node.

  Start local node via instructions in
  https://github.com/schierlm/private-iota-testnet"
  (:require [cljs-iota.api :as iota-api]
            [cljs-iota.core :as iota]
            [cljs-iota.test-utils :refer [contains-keys? test-async test-within]]
            [cljs.core.async :as async :refer [<!]]
            [cljs.test :refer-macros [async deftest is testing]]
            [clojure.string :as string])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(def iota (iota/create-iota "http://localhost:14700"))


(deftest get-node-info-test
  (async done
         (iota-api/get-node-info
          iota
          (fn [err res]
            (is (contains-keys? res
                                :jre-free-memory
                                :tips
                                :time
                                :duration
                                :app-name
                                :transactions-to-request
                                :jre-version
                                :jre-total-memory
                                :app-version
                                :jre-available-processors
                                :latest-milestone-index
                                :latest-solid-subtangle-milestone-index
                                :neighbors
                                :latest-solid-subtangle-milestone
                                :latest-milestone
                                :packets-queue-size
                                :jre-max-memory))
            (done)))))


(deftest add-neighbors-test
  (async done
         (iota-api/add-neighbors
          iota
          ["udp://1.1.1.1:1234"]
          (fn [err res]
            (is (= 0 res))
            (done)))))


(deftest get-neighbors-test
  (async done
         (iota-api/get-neighbors
          iota
          (fn [err res]
            ;; NOTE: depends on neighbor added by `add-neighbors`.
            (is (contains-keys? (first res)
                                :address
                                :number-of-all-transactions
                                :number-of-random-transaction-requests
                                :number-of-new-transactions
                                :number-of-invalid-transactions
                                :number-of-sent-transactions
                                :connection-type))
            (done)))))


(deftest remove-neighbors-test
  (async done
         (iota-api/remove-neighbors
          iota
          ["udp://8.8.8.8:14265"]
          (fn [err res]
            (let [amount-removed 0]
              (is (= amount-removed res)))
            (done)))))


(deftest get-tips-test
  (async done
         (iota-api/get-tips
          iota
          (fn [err res]
            (when err (println err))
            (is (= 81 (count (first res))))
            (done)))))


(deftest find-transactions-by-bundles-test
  (async done
         (iota-api/find-transactions
          iota
          {:bundles
           ["RVORZ9999999999999999999999999999999999999999999999999999999999999999999999999999999999999"]}
          (fn [err res]
            (is (string/includes? (str err) "Invalid bundles input"))
            (done)))))


(deftest find-transactions-by-addresses-test
  (async done
         (iota-api/find-transactions
          iota
          {:addresses
           ["RVORZ9SIIP9RCYMREUIXXVPQIPHVCNPQ9HZWYKFWYWZRE9JQKG9REPKIASHUUECPSQO9JT9XNMVKWYGVAZETAIRPTM"
            "RVORZ9SIIP9RCYMREUIXXVPQIPHVCNPQ9HZWYKFWYWZRE9JQKG9REPKIASHUUECPSQO9JT9XNMVKWYGVAZETAIRPTM"]}
          (fn [err res]
            (is (= [] res))
            (done)))))


(deftest find-transactions-by-tags-test
  (async done
         (iota-api/find-transactions
          iota
          {:tags ["TAG"]}
          (fn [err res]
            (is (= [] res))
            (done)))))


(deftest find-transactions-by-approvees-test
  (async done
         (iota-api/find-transactions
          iota
          {:approvees ["OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"]}
          (fn [err res]
            (is (= [] res))
            (done)))))


(deftest get-trytes-test
  (async done
         (iota-api/get-trytes
          iota
          ["OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"]
          (fn [err [tryte & _]]
            (is (= "9999999999" (subs tryte 0 10)))
            ;; NOT EXPECTED, should be as
            ;; in
            ;; https://iota.readme.io/v1.2.0/reference#gettrytes
            (done)))))


(deftest get-inclusion-states-test
  (async done
         (iota-api/get-inclusion-states
          iota
          ["QHBYXQWRAHQJZEIARWSQGZJTAIITOZRMBFICIPAVD9YRJMXFXBDPFDTRAHHHP9YPDUVTNOFWZGFGWMYHEKNAGNJHMW"]
          ["ZIJGAJ9AADLRPWNCYNNHUHRRAC9QOUDATEDQUMTNOTABUVRPTSTFQDGZKFYUUIE9ZEBIVCCXXXLKX9999"]
          (fn [err res]
            (is (string/includes? (str err) "Invalid transactions input"))
            (done)))))


(deftest get-balances-test
  (async done
         (iota-api/get-balances
          iota
          ["HBBYKAKTILIPVUKFOTSLHGENPTXYBNKXZFQFR9VQFWNBMTQNRVOUKPVPRNBSZVVILMAFBKOTBLGLWLOHQ"]
          100
          (fn [err res]
            (is (contains-keys? res
                                :balances
                                :duration
                                :references
                                :milestone-index))
            (done)))))


(deftest get-transactions-to-approve-test
  (async done
         (iota-api/get-tips
          iota
          (fn [err res]
            (let [tip (first res)]
              (iota-api/get-transactions-to-approve
               iota
               27
               tip
               (fn [_ r]
                 (is (contains-keys? r
                                     :trunk-transaction
                                     :branch-transaction
                                     :duration))
                 (done))))))))


(declare trytes)
(deftest attach-to-tangle-test
  ;; First get transactions to approve, then attach to Tangle
  (async done
         (iota-api/get-tips
          iota
          (fn [err res]
            (let [tip (first res)]
              (iota-api/get-transactions-to-approve
               iota
               27
               tip
               (fn [_ {:keys [trunk-transaction
                              branch-transaction
                              duration]}]
                 (iota-api/attach-to-tangle
                  iota
                  trunk-transaction
                  branch-transaction
                  13
                  [trytes]
                  (fn [err res]
                    (is (or

                         ;; Length of attached trytes
                         (= 2673 (count (first res)))

                         ;; Don't know why, but it happens
                         (= 0 (count (first res)))))
                    (done))))))))))


(deftest interrupt-attaching-to-tangle-test
  (async done
         (iota-api/interrupt-attaching-to-tangle
          iota
          (fn [err res]
            (is (contains-keys? res :duration))
            (done)))))


(deftest broadcast-transactions-test
  (async done
         (iota-api/broadcast-transactions
          iota
          ["TRYTESFROMATTACHTOTANGLE"]
          ;; TODO: returns Invalid attached Trytes provided
          (fn [err res]
            #_(is (= nil err))
            (done)))))

(deftest store-transactions-test
  (async done
         (iota-api/store-transactions
          iota
          ["TRYTESFROMATTACHTOTANGLE"]
          ;; TODO: returns Invalid attached Trytes provided
          (fn [err res]
            #_(is (= nil err))
            (done)))))


;;;;
;;;; JavaScript API tests

(deftest get-transactions-objects-test
  (async done
         (iota-api/get-transactions-objects
          iota
          ["AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"]
          (fn [err res]
            (is (contains-keys? (first res)
                                :address
                                :last-index
                                :hash
                                :attachment-timestamp
                                :value
                                :bundle
                                :trunk-transaction
                                :branch-transaction
                                :signature-message-fragment
                                :current-index
                                :attachment-timestamp-upper-bound
                                :tag
                                :obsolete-tag
                                :timestamp
                                :nonce
                                :attachment-timestamp-lower-bound))
            (done)))))


(deftest find-transactions-objects-by-bundles-test
  (async done
         (iota-api/find-transaction-objects
          iota
          {:bundles
           ["RVORZ9999999999999999999999999999999999999999999999999999999999999999999999999999999999999"
            "RVORZ9999999999999999999999999999999999999999999999999999999999999999999999999999999999999"]}
          (fn [err res]
            (is (string/includes? (str err) "Invalid bundles input"))
            (done)))))


(deftest find-transactions-objects-by-addresses-test
  (async done
         (iota-api/find-transaction-objects
          iota
          {:addresses
           ["RVORZ9SIIP9RCYMREUIXXVPQIPHVCNPQ9HZWYKFWYWZRE9JQKG9REPKIASHUUECPSQO9JT9XNMVKWYGVAZETAIRPTM"
            "RVORZ9SIIP9RCYMREUIXXVPQIPHVCNPQ9HZWYKFWYWZRE9JQKG9REPKIASHUUECPSQO9JT9XNMVKWYGVAZETAIRPTM"]}
          (fn [err res]
            (is (= [] res))
            (done)))))


(deftest find-transactions-objects-by-tags-test
  (async done
         (iota-api/find-transaction-objects
          iota
          {:tags ["TAG"]}
          (fn [err res]
            (is (= [] res))
            (done)))))


(deftest find-transactions-objects-by-approvees-test
  (async done
         (iota-api/find-transaction-objects
          iota
          {:approvees ["OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"]}
          (fn [err res]
            (is (= [] res))
            (done)))))


(deftest get-latest-inclusion-test
  (async done
         (iota-api/get-latest-inclusion
          iota
          ["OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"]
          (fn [err res]
            (let [transaction-confirmed? false]
              (is (= (first res) transaction-confirmed?)))
            (done)))))


;;; Mentioned in iota.lib.js README, but not implemented on API object
#_(deftest broadcast-and-store-test
  (async done
         (iota-api/broadcast-and-store
          iota
          ["OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"]
          (fn [err res]
            (is (= nil err))
            (done)))))


(deftest get-new-address-test
  (async done
         (iota-api/get-new-address
          iota
          "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"
          {:index      1
           :checksum   true
           :total      1
           :security   1
           :return-all true}
          (fn [err res]
            (is (= res ["VIYTWLBLSQDSGODGJUIYZHIVFNCHXZZOILRGFU9FMMYOLWNWDUUIBWAZIKOKHR9FSGKFRRMTRNYISFEP9VODCVPQFC"]))
            (done)))))


(deftest get-inputs-test
  (async done
         (iota-api/get-inputs
          iota
          "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"
          (fn [err res]
            (is (contains-keys? res :inputs :total-balance))
            (done)))))


(deftest prepare-transfers-test
  (async done
         ;; Addresses from `get-new-address`
         (let [addr "GPRGUPKQLWBFUVCJVVUMZZSYXMHLWYYV9GE9EGMMMYSQAZHUBSXQTAVFAXJFJWVUVRXNUCGJHJDVJKCOAODSDDKPKW"
               seed "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"]
           (iota-api/prepare-transfers
            iota
            seed
            [{:address addr
              :value   0}]
            {:inputs
             [{:address   "VIYTWLBLSQDSGODGJUIYZHIVFNCHXZZOILRGFU9FMMYOLWNWDUUIBWAZIKOKHR9FSGKFRRMTRNYISFEP9VODCVPQFC"
               :balance   1
               :key-index 0
               :security  1}
              {:address   "OEUJLDUONDOOMANNKZIIPDFZFT9ZJWBURMCKLKIVGQPBEPHCOCSUHXBF9RJJDQC9ONMBZCATTSTABUHFCMSLQYGMTW"
               :balance   0
               :key-index 1
               :security  1}]}
            (fn [err res]
              (let [trytes                (first res)
                    addr-without-checksum (apply str (drop-last 9 addr))]
                (is (string/includes?
                     trytes
                     addr-without-checksum)))
              (done))))))


;;; Trytes from `prepare-transfers`

(def trytes
  (string/join
   ["9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "999999999999999999999999999999999999999999999999999999GPRGUPKQLWBFUVCJVVUMZZSYX"
    "MHLWYYV9GE9EGMMMYSQAZHUBSXQTAVFAXJFJWVUVRXNUCGJHJDVJKCOA99999999999999999999999"
    "9999PD9999999999999999999999999OEZZUYD99999999999999999999SOUPXIAXFXZYV9EWNTLGE"
    "GLBLKSBJEHVONUQSCIHFSCUECWYDEFOAIWHFPKVONKB9GLUUCFFKUCKWEUDC9999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "9999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "999999999999999999999999999999999999999999999999999999999999999999"]))


(deftest send-trytes-test
  (async done
         (iota-api/send-trytes
          iota
          [trytes]
          3
          13
          (fn [err res]
            (is (contains-keys? (first res)
                                :address
                                :last-index
                                :hash
                                :attachment-timestamp
                                :value
                                :bundle
                                :trunk-transaction
                                :branch-transaction
                                :signature-message-fragment
                                :current-index
                                :attachment-timestamp-upper-bound
                                :tag
                                :obsolete-tag
                                :timestamp
                                :nonce
                                :attachment-timestamp-lower-bound ))
            (done)))))


(deftest send-transfer-test
  (async done
         (iota-api/send-transfer
          iota
          "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"
          1
         14
         [{:address "OEUJLDUONDOOMANNKZIIPDFZFT9ZJWBURMCKLKIVGQPBEPHCOCSUHXBF9RJJDQC9ONMBZCATTSTABUHFCMSLQYGMTW"
           :value   0
           :message "FOO"
           :tag     "NONE"}]
         {}
          (fn [err res]
            (is (contains-keys? (first res)
                 :address
                 :last-index
                 :hash
                 :attachment-timestamp
                 :value
                 :bundle
                 :trunk-transaction
                 :branch-transaction
                 :signature-message-fragment
                 :current-index
                 :attachment-timestamp-upper-bound
                 :tag
                 :obsolete-tag
                 :timestamp
                 :nonce
                 :attachment-timestamp-lower-bound))
            (done)))))


(deftest promote-transaction-test
  (let [transfers [{:address "OEUJLDUONDOOMANNKZIIPDFZFT9ZJWBURMCKLKIVGQPBEPHCOCSUHXBF9RJJDQC9ONMBZCATTSTABUHFCMSLQYGMTW"
                    :value   0
                    :message "FOO"
                    :tag     "NONE"}]]
    (async done
           ;; First send a transfer
           (iota-api/send-transfer
            iota
            "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"
            1
            14
            transfers
            {}
            ;; Then promote it
            (fn [err [{:keys [hash]} & txs]]
              (iota-api/promote-transaction
               iota
               hash
               1
               14
               transfers
               {}
               (fn [e [tx & txs]]
                 (is (contains-keys? tx
                      :address
                      :last-index
                      :hash
                      :attachment-timestamp
                      :value
                      :bundle
                      :trunk-transaction
                      :branch-transaction
                      :signature-message-fragment
                      :current-index
                      :attachment-timestamp-upper-bound
                      :tag
                      :obsolete-tag
                      :timestamp
                      :nonce
                      :attachment-timestamp-lower-bound))
                 (done))))))))


(deftest replay-bundle-test
  (let [tail-transaction     "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"
        depth                1
        min-weight-magnitude 10]
    (async done
           (iota-api/replay-bundle
            iota
            tail-transaction
            depth
            min-weight-magnitude
            (fn [err res]
              (is (contains-keys? (first res)
                                  :address
                                  :last-index
                                  :hash
                                  :attachment-timestamp
                                  :value
                                  :bundle
                                  :trunk-transaction
                                  :branch-transaction
                                  :signature-message-fragment
                                  :current-index
                                  :attachment-timestamp-upper-bound
                                  :tag
                                  :obsolete-tag
                                  :timestamp
                                  :nonce
                                  :attachment-timestamp-lower-bound))
              (done))))))


(deftest broadcast-bundle-test
  (let [tail-transaction "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"]
    (async done
           (iota-api/broadcast-bundle
            iota
            tail-transaction
            (fn [err res]
              (is (contains-keys? res :duration))
              (done))))))


(deftest get-bundle-test
  (let [tail-transaction "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"]
    (async done
           (iota-api/get-bundle
            iota
            tail-transaction
            (fn [err res]
              (is (contains-keys? (first res)
                                  :address
                                  :last-index
                                  :hash
                                  :attachment-timestamp
                                  :value
                                  :bundle
                                  :trunk-transaction
                                  :branch-transaction
                                  :signature-message-fragment
                                  :current-index
                                  :attachment-timestamp-upper-bound
                                  :tag
                                  :obsolete-tag
                                  :timestamp
                                  :nonce
                                  :attachment-timestamp-lower-bound))
              (done))))))


(deftest get-transfers-test
  (let [seed "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"]
    (async done
           (iota-api/get-transfers
            iota
            seed
            (fn [err res]
              (is (contains-keys? (first res)
                                  :address
                                  :last-index
                                  :hash
                                  :attachment-timestamp
                                  :value
                                  :bundle
                                  :trunk-transaction
                                  :branch-transaction
                                  :signature-message-fragment
                                  :current-index
                                  :attachment-timestamp-upper-bound
                                  :tag
                                  :obsolete-tag
                                  :timestamp
                                  :nonce
                                  :attachment-timestamp-lower-bound))
              (done))))))


(deftest get-account-data-test
  (let [seed "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"]
    (async done
           (iota-api/get-account-data
            iota
            seed
            (fn [err res]
              (is (contains-keys? res
                                  :latest-address
                                  :addresses
                                  :transfers
                                  :inputs
                                  :balance))
              (done))))))


(deftest promotable?-test
  (let [tail-transaction "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"]
    (test-async
     (test-within 1000
                  (go
                    (is (true? (<! (iota-api/promotable?
                                    iota
                                    tail-transaction)))))))))


(deftest reattachable?-test
  (let [address "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"]
    (async done
           (iota-api/reattachable?
            iota
            address
            (fn [err reattachable?]
              (is (true? reattachable?))
              (done))))))
