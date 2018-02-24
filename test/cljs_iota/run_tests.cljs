(ns cljs-iota.run-tests
  (:require [cljs-iota.api-test]
            [cljs-iota.core-test]
            [cljs-iota.multisig-test]
            [cljs-iota.utils-test]
            [cljs-iota.valid-test]
            [cljs.test :refer-macros [run-tests]]))


(defn run-all-tests []
  (enable-console-print!)
  (.clear js/console)
  (run-tests 'cljs-iota.core-test
             'cljs-iota.api-test
             'cljs-iota.utils-test
             'cljs-iota.multisig-test
             'cljs-iota.valid-test))


(comment
  (run-all-tests))
