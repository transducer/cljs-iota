(ns cljs-iota.multisig-test
  "Unit tests that are running against a local node.

  Start local node via instructions in
  https://github.com/schierlm/private-iota-testnet"
  (:require [cljs-iota.core :as iota]
            [cljs-iota.multisig :as iota-multisig]
            [cljs-iota.test-utils :refer [contains-keys? test-async test-within]]
            [cljs.test :refer-macros [async deftest is testing]]
            [clojure.string :as string]))


(def iota (iota/create-iota "http://localhost:14700"))


;; (deftest get-key-test
;;   (let [seed "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"]
;;     (is (= 2187 (count (iota-multisig/get-key iota seed 0 1))))
;;     (is (= 4374 (count (iota-multisig/get-key iota seed 0 2))))
;;     (is (= 6561 (count (iota-multisig/get-key iota seed 0 3))))))


(deftest get-digest-test
  (let [seed "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"]
    (is (= 81 (count (iota-multisig/get-digest iota seed 0 1))))
    (is (= 162 (count (iota-multisig/get-digest iota seed 0 2))))
    (is (= 243 (count (iota-multisig/get-digest iota seed 0 3))))))


(deftest address-test
  (let [digest1        "GUIGQXBUBA9FLS9KLC9CTMMVYOMMSZVGBNDXNNDHOAWLCMYUXOZQOLZJTJSVETPPISBWWZXBQDORJSWF9"
        digest2        "ITXHHSXFHDXQSEDSAFNXJFRXFWYCEFRCBQRBXXYDFWTPDCANDPNRAWXDJXZWRPABCHURMOJMGAMYRBZQC"
        address-object (-> (js/IOTA.) (aget "multisig") (aget "address"))]
    (is (= address-object
           (type (iota-multisig/address iota [digest1 digest2]))))))


(deftest absorb-test
  (let [digest1          "GUIGQXBUBA9FLS9KLC9CTMMVYOMMSZVGBNDXNNDHOAWLCMYUXOZQOLZJTJSVETPPISBWWZXBQDORJSWF9"
        digest2          "ITXHHSXFHDXQSEDSAFNXJFRXFWYCEFRCBQRBXXYDFWTPDCANDPNRAWXDJXZWRPABCHURMOJMGAMYRBZQC"
        multisig-address (iota-multisig/address iota digest1)
        address-object   (-> (js/IOTA.) (aget "multisig") (aget "address"))]
    (is (= address-object
           (type (iota-multisig/absorb multisig-address digest2))))))


(deftest finalize-test
  (let [digest1          "GUIGQXBUBA9FLS9KLC9CTMMVYOMMSZVGBNDXNNDHOAWLCMYUXOZQOLZJTJSVETPPISBWWZXBQDORJSWF9"
        digest2          "ITXHHSXFHDXQSEDSAFNXJFRXFWYCEFRCBQRBXXYDFWTPDCANDPNRAWXDJXZWRPABCHURMOJMGAMYRBZQC"
        multisig-address (iota-multisig/address iota [digest1 digest2])
        address          "YBHSKCLLCTAHFKXWIAATNALMBIUJJPLGBQIKJOBKLURPAWNPTVXXPWOPRGLHIAXVOSOAIAUPB9AGVYCGW"]
    (is (= address (iota-multisig/finalize multisig-address)))))


(deftest validate-address-test
  (let [digest1 "GUIGQXBUBA9FLS9KLC9CTMMVYOMMSZVGBNDXNNDHOAWLCMYUXOZQOLZJTJSVETPPISBWWZXBQDORJSWF9"
        digest2 "ITXHHSXFHDXQSEDSAFNXJFRXFWYCEFRCBQRBXXYDFWTPDCANDPNRAWXDJXZWRPABCHURMOJMGAMYRBZQC"
        address "YBHSKCLLCTAHFKXWIAATNALMBIUJJPLGBQIKJOBKLURPAWNPTVXXPWOPRGLHIAXVOSOAIAUPB9AGVYCGW"]

    (testing "Valid when in order of adding digests (see finalize-test for address generation)"
      (is (true? (iota-multisig/validate-address iota address [digest1 digest2]))))

    (testing "Invalid when order of digest validation differs from the order of addition on creation"
      (is (false? (iota-multisig/validate-address iota address [digest2 digest1]))))

    ;; TODO: is this expected behaviour?
    (testing "Error when wrong amount of digests"
      (is (thrown-with-msg? js/Error #"Cannot read property"
                            (iota-multisig/validate-address iota address ["xxx"]))))))


;;; Transfers returned from `iota-api/get-transfers` with seed
;;; "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"

(def transfers
  [{:address                          "GPRGUPKQLWBFUVCJVVUMZZSYXMHLWYYV9GE9EGMMMYSQAZHUBSXQTAVFAXJFJWVUVRXNUCGJHJDVJKCOA"
    :last-index                       0
    :hash                             "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"
    :attachment-timestamp             1518970638562
    :value                            0 ;; no balance on address TODO
    :bundle                           "SOUPXIAXFXZYV9EWNTLGEGLBLKSBJEHVONUQSCIHFSCUECWYDEFOAIWHFPKVONKB9GLUUCFFKUCKWEUDC"
    :trunk-transaction                "VIBDGRFTQ9SQDVAVKYJSQCDX9NCXRPT9NLTENEYARZYQGPPDVFWS9EA9ZMPXLGFRLJEGBYBGV9PGZ9999"
    :branch-transaction               "GKVQLV9ORKHJACTYHVZHFTCBJOCMVAJHGYKQ9HVAEE9WVMJS9MCVKOFKEMDFIMJZFJMCEEFXBNAWZ9999"
    :signature-message-fragment       (str "ODGABCCCKBCCDCBCGADBEAGABCDCMBMBOBBCBCGAQD" (apply str (repeat 2145 "9")))
    :current-index                    0
    :attachment-timestamp-upper-bound 12
    :tag                              "PD9999999999999999999999999"
    :obsolete-tag                     "PD9999999999999999999999999"
    :timestamp                        1517775207
    :nonce                            "IEDZBBJENMHCTMNWUJTPDKDITBK"
    :attachment-timestamp-lower-bound 0}])


(def transfers-with-balance
  (map #(assoc % :value 1) transfers))


(deftest initiate-transfer-test
  (let [digest1          "GUIGQXBUBA9FLS9KLC9CTMMVYOMMSZVGBNDXNNDHOAWLCMYUXOZQOLZJTJSVETPPISBWWZXBQDORJSWF9"
        digest2          "ITXHHSXFHDXQSEDSAFNXJFRXFWYCEFRCBQRBXXYDFWTPDCANDPNRAWXDJXZWRPABCHURMOJMGAMYRBZQC"
        multisig-address (iota-multisig/address iota [digest1 digest2])
        address          "YBHSKCLLCTAHFKXWIAATNALMBIUJJPLGBQIKJOBKLURPAWNPTVXXPWOPRGLHIAXVOSOAIAUPB9AGVYCGW"]

    ;; TODO: add balance to address
    (iota-multisig/initiate-transfer iota {:address      address
                                           :security-sum 2}
                                     nil
                                     transfers-with-balance
                                     (fn [err res]
                                       (is (string/includes? (str err) "Not enough balance"))))))


(deftest add-signature-test
  (let [digest1          "GUIGQXBUBA9FLS9KLC9CTMMVYOMMSZVGBNDXNNDHOAWLCMYUXOZQOLZJTJSVETPPISBWWZXBQDORJSWF9"
        digest2          "ITXHHSXFHDXQSEDSAFNXJFRXFWYCEFRCBQRBXXYDFWTPDCANDPNRAWXDJXZWRPABCHURMOJMGAMYRBZQC"
        multisig-address (iota-multisig/address iota [digest1 digest2])
        address          "YBHSKCLLCTAHFKXWIAATNALMBIUJJPLGBQIKJOBKLURPAWNPTVXXPWOPRGLHIAXVOSOAIAUPB9AGVYCGW"
        seed             "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"]

    (iota-multisig/add-signature iota
                                 transfers
                                 address
                                 seed ;; ?
                                 (fn [err res]
                                   (let [transfer (first res)]
                                     (contains-keys? transfer
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
                                                     :attachment-timestamp-lower-bound))))))
