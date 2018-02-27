(ns cljs-iota.js-utils
  "Utilities for interacting with JavaScript.

  Copied from cljs-web3 by district0x."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [camel-snake-kebab.core :as kebab :include-macros true]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [clojure.string :as string]
            [cljs.core.async :as async :refer [>! chan]]))


(defn safe-case [case-fn]
  (fn [x]
    (cond-> (subs (name x) 1)
      true         (string/replace "_" "*")
      true         case-fn
      true         (string/replace "*" "_")
      true         (->> (str (first (name x))))
      (keyword? x) keyword)))


(def camel-case (safe-case kebab/->camelCase))
(def kebab-case (safe-case kebab/->kebab-case))


(def js->cljk #(js->clj % :keywordize-keys true))


(def js->cljkk
  "From JavaScript to Clojure with kekab-cased keywords."
  (comp (partial transform-keys kebab-case) js->cljk))


(def cljkk->js
  "From Clojure to JavaScript object with camelCase keys."
  (comp clj->js (partial transform-keys camel-case)))


(defn callback-js->clj [x]
  (if (fn? x)
    (fn [err res]
      (when (and res (aget res "v"))
        (aset res "v" (aget res "v"))) ;; Prevent weird bug in advanced optimizations
      (x err (js->cljkk res)))
    x))


(defn args-cljkk->js [args]
  (map (comp cljkk->js callback-js->clj) args))


(defn js-apply
  "Applies method on JavaScript object. Arguments and return value are
  Clojure style (kebab-case)."
  ([this method-name]
   (js-apply this method-name nil))
  ([this method-name args]
   (let [method-name (camel-case (name method-name))]
     (if (aget this method-name)
       (js->cljkk (apply js-invoke this method-name (args-cljkk->js args)))
       (throw (str "Method: " method-name " was not found in object."))))))


(defn js-prototype-apply [js-obj method-name args]
  (js-apply (aget js-obj "prototype") method-name args))


(defn prop-or-clb-fn
  "Constructor to create an fn to get properties or to get properties and apply
  a callback function."
  [& ks]
  (fn [iota & args]
    (if (fn? (first args))
      (js-apply (apply aget iota (butlast ks))
                (str "get" (kebab/->PascalCase (last ks)))
                args)
      (js->cljkk (apply aget iota ks)))))


(defn create-async-fn
  "Creates a function that returns a core.async channel that will receive
  [err res]."
  [f]
  (fn [& args]
    (let [[ch args] (if (instance? cljs.core.async.impl.channels/ManyToManyChannel (first args))
                      [(first args) (rest args)]
                      [(chan) args])]
      (apply f (concat args [(fn [err res]
                               (go (>! ch [err res])))]))
      ch)))
