(ns cljs-iota.run-tests
  (:require [cljs-iota.tests]
            [cljs.test :refer-macros [run-tests]]))


(defn run-all-tests []
  (.clear js/console)
  (run-tests 'cljs-iota.tests))


(run-all-tests)
