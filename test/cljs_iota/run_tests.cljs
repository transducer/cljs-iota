(ns cljs-iota.run-tests
  (:require [cljs-iota.api-test]
            [cljs-iota.core-test]
            [cljs.test :refer-macros [run-tests]]))


(defn run-all-tests []
  (enable-console-print!)
  (.clear js/console)
  (run-tests 'cljs-iota.core-test 'cljs-iota.api-test))


(run-all-tests)
