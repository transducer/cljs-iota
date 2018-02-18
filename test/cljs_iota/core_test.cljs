(ns cljs-iota.core-test
  "Unit tests that are running against a local node.

  Start local node via instructions in
  https://github.com/schierlm/private-iota-testnet"
  (:require [cljs-iota.core :as iota]
            [cljs.test :refer-macros [async deftest is testing]]))


(def iota (iota/create-iota "http://localhost:14700"))


(deftest core-tests
  (is (= (iota/version iota) "0.4.6"))
  (is (= (iota/host iota) "http://localhost"))
  (is (= (iota/port iota) 14265))
  (is (= (iota/provider iota) "http://localhost:14700"))
  (is (= (iota/sandbox iota) false))
  (is (= (iota/token iota) false)))
