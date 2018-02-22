(ns cljs-iota.utils-test
  "Unit tests that are running against a local node.

  Start local node via instructions in
  https://github.com/schierlm/private-iota-testnet"
  (:require [cljs-iota.core :as iota]
            [cljs-iota.test-utils :refer [contains-keys? test-async test-within]]
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
