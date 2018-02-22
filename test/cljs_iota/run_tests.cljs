(ns cljs-iota.run-tests
  (:require [cljs-iota.api-test]
            [cljs-iota.core-test]
            [cljs-iota.utils-test]
            [cljs.test :refer-macros [run-tests]]))


(defn run-all-tests []
  (enable-console-print!)
  (.clear js/console)
  (run-tests #_'cljs-iota.core-test #_'cljs-iota.api-test 'cljs-iota.utils-test))


(run-all-tests)
