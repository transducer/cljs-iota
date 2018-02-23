(ns cljs-iota.valid-test
  "Unit tests that are running against a local node.

  Start local node via instructions in
  https://github.com/schierlm/private-iota-testnet"
  (:require [cljs-iota.core :as iota]
            [cljs-iota.test-utils :refer [contains-keys? test-async test-within]]
            [cljs-iota.valid :as iota-valid]
            [cljs.test :refer-macros [async deftest is testing]]
            [clojure.string :as string]))


(def iota (iota/create-iota "http://localhost:14700"))


(deftest address?-test
  (let [address-with-checksum    "UGSNHG9ULXTTHADQZZJDIWQSAPHFIRSMHJFADTJRJCDAKGNCEEUPYAF99CXFEZQDXRNQVCL9MFKNNUBZWISZJXTWEC"
        address-without-checksum (apply str (drop-last 9 address-with-checksum))]
    (is (iota-valid/address? iota address-with-checksum))
    (is (iota-valid/address? iota address-without-checksum))
    (is (not (iota-valid/address? iota (drop 1 address-with-checksum))))))


(deftest trytes?-test
  (let [trytes         "ABC9"
        invalid-trytes "ABC0"]
    (is (iota-valid/trytes? iota trytes))
    (is (not (iota-valid/trytes? iota invalid-trytes)))
    (is (iota-valid/trytes? iota trytes 4))
    (is (not (iota-valid/trytes? iota trytes 5)))))


(deftest value?-test
  (let [value1       10
        value2       -10
        not-a-value1 11.1
        not-a-value2 "11"]
    (is (iota-valid/value? iota value1))
    (is (iota-valid/value? iota value2))
    (is (not (iota-valid/value? iota not-a-value1)))
    (is (not (iota-valid/value? iota not-a-value2)))))


(deftest num?-test
  (let [num1       10
        num2       "11.1"
        num3       11.1
        not-a-num1 "a"
        not-a-num2 "1a"
        not-a-num3 -10] ;; TODO, why negative values not a num?
    (is (iota-valid/num? iota num1))
    (is (iota-valid/num? iota num2))
    (is (iota-valid/num? iota num3))
    (is (not (iota-valid/num? iota not-a-num1)))
    (is (not (iota-valid/num? iota not-a-num2)))
    (is (not (iota-valid/num? iota not-a-num3)))))


(deftest hash?-test
  (let [hash         (apply str (repeat 81 "9"))
        invalid-hash (apply str (repeat 82 "9"))]
    (is (iota-valid/hash? iota hash))
    (is (not (iota-valid/hash? iota invalid-hash)))))


(deftest transfers?-test
  (let [address          (apply str (repeat 81 "9"))
        value            1
        message          (apply str (repeat 10 "9"))
        tag              (apply str (repeat 27 "9"))
        transfer         {:address address
                          :value   value
                          :message message
                          :tag     tag}
        invalid-transfer {:address "9" ;; invalid address
                          :value   value
                          :message message
                          :tag     tag}]
    (is (iota-valid/transfers? iota [transfer]))
    (is (not (iota-valid/transfers? iota [invalid-transfer])))))


(deftest hashes?-test
  (let [hash         (apply str (repeat 81 "9"))
        invalid-hash (apply str (repeat 82 "9"))]
    (is (iota-valid/hashes? iota [hash hash]))
    (is (not (iota-valid/transfers? iota [hash invalid-hash])))))


;;; Trytes from `iota-api/prepare-transfers`

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


(deftest trytes-coll?-test
  (is (iota-valid/trytes-coll? iota [trytes trytes]))
  (is (iota-valid/trytes-coll? iota []))
  (is (not (iota-valid/trytes-coll? iota [(str trytes "9")]))))


;;; Attached trytes from `iota-api/attach-to-tangle`

(def attached-trytes
  (string/join
   ["99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "99999999999999999999999999999999999999999999999999999999999999999999999999999999"
    "999999999999999999999999999GPRGUPKQLWBFUVCJVVUMZZSYXMHLWYYV9GE9EGMMMYSQAZHUBSXQT"
    "AVFAXJFJWVUVRXNUCGJHJDVJKCOA999999999999999999999999999PD99999999999999999999999"
    "99OEZZUYD99999999999999999999SOUPXIAXFXZYV9EWNTLGEGLBLKSBJEHVONUQSCIHFSCUECWYDEF"
    "OAIWHFPKVONKB9GLUUCFFKUCKWEUDCJXPEIYQNGDWD9RKKVHDDPIWEPQATBHMCUVFQOBY9CCP9BZBKKK"
    "GKKKEWZZROYJRQJSYRDDFHABNIHX999OCQAIUQUU9WUACVGNVQQUPUCVLMZCRMVNTDFLXFBXCOIATFGL"
    "NDAEGQRMJNBBJQHYPDBQHFSKCDBJW999PD9999999999999999999999999BJDHIXGJE999999999L99"
    "999999LRIRHQQWORBIWKCOEROQKARAJOO"]))


(deftest attached-trytes-coll?-test
  (is (iota-valid/attached-trytes-coll? iota [attached-trytes attached-trytes]))
  (is (iota-valid/attached-trytes-coll? iota []))
  (is (not (iota-valid/attached-trytes-coll? iota [trytes trytes]))))


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


(deftest transactions?-test
  (is (iota-valid/transactions? iota transfers))
  (is (not (iota-valid/transactions? iota [])))
  (is (not (iota-valid/transactions? iota [trytes]))))


;;; inputs for `iota-api/prepare-transfers-test`

(def inputs
  [{:address   "VIYTWLBLSQDSGODGJUIYZHIVFNCHXZZOILRGFU9FMMYOLWNWDUUIBWAZIKOKHR9FSGKFRRMTRNYISFEP9VODCVPQFC"
    :balance   1
    :key-index 0
    :security  1}
   {:address   "OEUJLDUONDOOMANNKZIIPDFZFT9ZJWBURMCKLKIVGQPBEPHCOCSUHXBF9RJJDQC9ONMBZCATTSTABUHFCMSLQYGMTW"
    :balance   0
    :key-index 1
    :security  1}])


(deftest inputs?-test
  (is (iota-valid/inputs? iota inputs))
  (is (iota-valid/inputs? iota []))

  ;; TODO: Should this be okay?
  (is (iota-valid/inputs? iota (map #(dissoc % :balance) inputs)))

  (is (not (iota-valid/inputs? iota (map #(dissoc % :address) inputs)))))


(deftest iota-string?-test
  (is (iota-valid/iota-string? iota "a"))
  (is (iota-valid/iota-string? iota \a))
  (is (not (iota-valid/iota-string? iota [])))
  (is (not (iota-valid/iota-string? iota transfers))))


(deftest iota-array?-test
  (is (iota-valid/iota-coll? iota ["a"]))
  (is (iota-valid/iota-coll? iota [\a]))
  (is (iota-valid/iota-coll? iota []))
  (is (iota-valid/iota-coll? iota transfers))
  (is (not (iota-valid/iota-coll? iota "a"))))


(deftest iota-obj?-test
  (is (iota-valid/iota-obj? iota ["a"]))
  (is (iota-valid/iota-obj? iota [\a]))
  (is (iota-valid/iota-obj? iota []))
  (is (iota-valid/iota-obj? iota {:foo "bar"}))
  (is (iota-valid/iota-obj? iota {}))
  (is (iota-valid/iota-obj? iota #{}))
  (is (iota-valid/iota-obj? iota transfers))
  (is (not (iota-valid/iota-obj? iota "a"))))


(deftest uri?-test
  (is (iota-valid/iota-uri? iota "udp://[2001:db8:a0b:12f0::1]:14265"))
  (is (iota-valid/iota-uri? iota "udp://[2001:db8:a0b:12f0::1]"))
  (is (iota-valid/iota-uri? iota "udp://8.8.8.8:14265"))
  (is (iota-valid/iota-uri? iota "udp://domain.com"))
  (is (iota-valid/iota-uri? iota "udp://domain2.com:14265"))
  (is (not (iota-valid/iota-uri? iota #{})))
  (is (not (iota-valid/iota-uri? iota transfers)))
  (is (not (iota-valid/iota-uri? iota "a"))))
