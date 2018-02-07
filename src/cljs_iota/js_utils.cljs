(ns cljs-iota.js-utils
  "Utilities for interacting with JavaScript.

  Copied from cljs-web3 by district0x."
  (:require [camel-snake-kebab.core :as cs :include-macros true]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [clojure.string :as string]))


(defn safe-case [case-fn]
  (fn [x]
    (cond-> (subs (name x) 1)
      true         (string/replace "_" "*")
      true         case-fn
      true         (string/replace "*" "_")
      true         (->> (str (first (name x))))
      (keyword? x) keyword)))


(def camel-case (safe-case cs/->camelCase))
(def kebab-case (safe-case cs/->kebab-case))


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
                (str "get" (cs/->PascalCase (last ks)))
                args)
      (js->cljkk (apply aget iota ks)))))
