(ns cljs-iota.utils-test
  "Unit tests that are running against a local node.

  Start local node via instructions in
  https://github.com/schierlm/private-iota-testnet"
  (:require [cljs-iota.core :as iota]
            [cljs-iota.test-utils :refer [contains-keys?]]
            [cljs-iota.utils :as iota-utils]
            [cljs.test :refer-macros [async deftest is testing]]
            [clojure.string :as string]))


(def iota (iota/create-iota "http://localhost:14700"))


(deftest convert-units-test
  (is (= (iota-utils/convert-units iota 1000 :i :ki)
         1))
  (is (= (iota-utils/convert-units iota 1 :ki :i)
         1000))
  (is (= (iota-utils/convert-units iota "1" :ki :i)
         1000))
  (is (= (iota-utils/convert-units iota 1.1 :ki :i)
         1100))
  (is (= (iota-utils/convert-units iota "1.1" :ki :i)
         1100))
  (is (thrown-with-msg? js/Error #"Invalid unit provided"
                        (iota-utils/convert-units iota "1" :xxx :i)))
  (is (thrown-with-msg? js/Error #"not a number"
                        (iota-utils/convert-units iota "a" :ki :i))))


(deftest add-checksum-test
  (let [address1  "UGSNHG9ULXTTHADQZZJDIWQSAPHFIRSMHJFADTJRJCDAKGNCEEUPYAF99CXFEZQDXRNQVCL9MFKNNUBZW"
        address2  "WPSSQLTNCMIB9AS9CAHHEASEKONMPGMNUIJFSGLJLDQNNTIOBSQHMGGTTPMJZHDHOJDDYFP9FATIQHEOW"
        checksum1 "ISZJXTWEC"
        checksum2 "XVEURTIJX"]
    (is (= (iota-utils/add-checksum iota address1 :checksum-length 9 :is-address true)
           (str address1 checksum1)))
    (is (= (iota-utils/add-checksum iota [address1 address2])
           [(str address1 checksum1) (str address2 checksum2)]))))


(deftest valid-checksum?-test
  (let [address-with-checksum    "UGSNHG9ULXTTHADQZZJDIWQSAPHFIRSMHJFADTJRJCDAKGNCEEUPYAF99CXFEZQDXRNQVCL9MFKNNUBZWISZJXTWEC"
        address-invalid-checksum "UGSNHG9ULXTTHADQZZJDIWQSAPHFIRSMHJFADTJRJCDAKGNCEEUPYAF99CXFEZQDXRNQVCL9MFKNNUBZWISZJXTWED"]
    (is (iota-utils/valid-checksum? iota address-with-checksum))
    (is (not (iota-utils/valid-checksum? iota address-invalid-checksum)))))


;;; Transaction trytes from `iota-api/prepare-transfers` with address
;;; "GPRGUPKQLWBFUVCJVVUMZZSYXMHLWYYV9GE9EGMMMYSQAZHUBSXQTAVFAXJFJWVUVRXNUCGJHJDVJKCOAODSDDKPKW"
;;; and seed
;;; "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999"

(def transaction-trytes
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


(deftest transaction-object-and-transaction-trytes-test
  (let [transaction-object (iota-utils/transaction-object iota transaction-trytes)]

    (testing "transaction-object"
      (is (contains-keys? transaction-object
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
                          :attachment-timestamp-lower-bound)))

    (testing "transaction-trytes"
      (is (= (iota-utils/transaction-trytes iota transaction-object)
             transaction-trytes)))))


;;; Transfers returned from `iota-api/get-transfers` with seed
;;; "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"

(def transfers
  [{:address                          "GPRGUPKQLWBFUVCJVVUMZZSYXMHLWYYV9GE9EGMMMYSQAZHUBSXQTAVFAXJFJWVUVRXNUCGJHJDVJKCOA"
    :last-index                       0
    :hash                             "PWWWAKUJYRHFLVDCIOFKAKBUYTPUXQMBYCYSB9WIFY9WCGNZKOH9DOECZEZURIGRTRXFKNUZONGFA9999"
    :attachment-timestamp             1518970638562
    :value                            0
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


(deftest categorize-transfers-test
  (let [{:keys [sent received] :as categorized}
        (iota-utils/categorize-transfers iota
                                         [transfers]
                                         (keep :address transfers))]
    (is (empty? sent))
    (is (= received [transfers]))))


(deftest to-trytes-test
  (testing "valid ASCII string"
    (is (= "LBOBKBDCCCSBPBDCVB" (iota-utils/to-trytes iota "BEAUTIFUL"))))

  (testing "something else"
    (is (nil? (iota-utils/to-trytes iota 1)))))


(deftest from-trytes-test
  (testing "valid trytes"
    (is (= "BEAUTIFUL" (iota-utils/from-trytes iota "LBOBKBDCCCSBPBDCVB"))))

  (testing "invalid trytes"
    (is (nil? (iota-utils/from-trytes iota "INVALID")))))


(deftest extract-json-test
  (is (= "{\"STATUS\": \"SUCCESS\"}"
         (iota-utils/extract-json iota transfers))))


(deftest validate-signatures-test
  ;; TODO: true?
  (is (false? (iota-utils/validate-signatures iota
                                              transfers
                                              (keep :address transfers)))))


(deftest bundle?-test
  (testing "valid bundle"
    (is (true? (iota-utils/bundle? iota transfers))))

  (testing "invalid bundle (without address)"
    (is (false? (iota-utils/bundle? iota (map #(dissoc % :address) transfers))))))
