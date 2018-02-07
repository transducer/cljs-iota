(ns cljs-iota.core
  "ClojureScript wrapper around IOTA JavaScript API methods on the IOTA object.

  An `iota` instance can be obtained via `create-iota`:

  ```
  (def iota-instance
    (create-iota \"http://localhost\" 14265))
  ```"
  (:require [cljs-iota.js-utils :as js-utils]
            cljsjs.iota))


(defn create-iota
  "Creates an IOTA instance.

  Create IOTA instance directly with provider:

  Parameters:
  provider - Full node URI. If not set, will be generated automatically from
            `host` and `port`.


  Create IOTA instance with host and port as provider:

  Parameters:
  host - Protocol and hostname of node (e.g., \"http://localhost\")
  port - API port number of node (e.g., 14265)

  Example:
  user> `(create-iota \"http://localhost:14265/\")`
  #object[IOTA [object Object]]"
  ([provider]
   (js/IOTA. #js {:provider provider}))
  ([host port]
   (js/IOTA. #js {:host host :port port})))


(def version
  "Returns a string representing the current version of the IOTA library.

  Parameters:
  iota - IOTA instance

  Example:
  user> `(iota/version iota-instance)`
  \"0.4.6\""
  (js-utils/prop-or-clb-fn "version"))


(def host
  "Returns a string representing the current host of the IOTA node.

  Parameters:
  iota - IOTA instance

  Example:
  user> `(iota/host iota-instance)`
  \"http://localhost\""
  (js-utils/prop-or-clb-fn "host"))


(def port
  "Returns an integer representing the current port of the IOTA node.

  Parameters:
  IOTA - IOTA instance

  Example:
  user> `(iota/port iota-instance)`
  14265"
  (js-utils/prop-or-clb-fn "port"))


(def provider
  "Returns a string representing the current provider of the IOTA library.

  Parameters:
  iota - IOTA instance

  Example:
  user> `(iota/provider iota-instance)`
  \"http://localhost:14265\""
  (js-utils/prop-or-clb-fn "provider"))


(def sandbox
  "Returns a boolean indicating if the IOTA node is a sandbox mode.

  See https://dev.iota.org/sandbox.

  Parameters:
  iota - IOTA instance

  Example:
  user> `(iota/sandbox iota-instance)`
  false"
  (js-utils/prop-or-clb-fn "sandbox"))


(def token
  "Returns a string representing the Auth token or false.

  Auth token (only used if `sandbox` is true).

  Parameters:
  iota - IOTA instance

  Example:
  user> `(iota/token iota-instance)`
  false"
  (js-utils/prop-or-clb-fn "token"))
