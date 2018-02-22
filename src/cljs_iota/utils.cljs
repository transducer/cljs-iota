(ns cljs-iota.utils
  "Utility related functions for conversions, validation and so on.
  All utils function are done synchronously."
  (:require [cljs-iota.js-utils :as js-utils]
            [clojure.string :as string]))


(defn utils
  "Gets utils object from IOTA library instance.

  Parameter:
  iota - IOTA client instance"
  [iota]
  (aget iota "utils"))


(defn convert-units
  "IOTA utilizes the Standard system of Units. See below for all available
  units:

  :i   :   1,
  :ki  :   1000,
  :mi  :   1000000,
  :gi  :   1000000000,
  :ti  :   1000000000000,
  :pi  :   1000000000000000

  Arguments:
  iota - IOTA client instance
  value - number || string Value to be converted. Can be string, an integer or
                           float (a number).
  from-unit - keyword Current unit of the value. See above for the available units
              to utilize for conversion.
  to-unit - keyword Unit to convert the from value into

  Returns the converted unit (from-unit => to-unit). "
  [iota value from-unit to-unit]
  (letfn [(stringify-unit [k]
            (if (= (count (name k)) 1)
              (name k)
              (string/capitalize (name k))))]
    (js-utils/js-apply (utils iota) "convertUnits"
                       [value
                        (stringify-unit from-unit)
                        (stringify-unit to-unit)])))
