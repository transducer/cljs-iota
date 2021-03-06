(defproject cljs-iota "1.0.1"
  :description    "ClojureScript API for IOTA JavaScript API"

  :url            "https://github.com/transducer/cljs-iota"

  :license        {:name "Eclipse Public License"
                   :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies   [[org.clojure/clojurescript "1.9.946"]
                   [camel-snake-kebab "0.4.0"]
                   [cljsjs/iota "0.4.7-0"]]

  :plugins        [[lein-cljsbuild "1.1.7"]]

  :figwheel       {:server-port 6612}

  :clean-targets  ^{:protect false} ["resources/public/js/compiled" "target"]

  :repl-options   {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :resource-paths []

  :profiles       {:dev
                   {:dependencies   [[binaryage/devtools "0.9.9"]
                                     [com.cemerick/piggieback "0.2.2"]
                                     [figwheel-sidecar "0.5.14"]
                                     [org.clojure/clojure "1.8.0"]
                                     [org.clojure/tools.nrepl "0.2.12"]]
                    :plugins        [[lein-figwheel "0.5.14"]]
                    :source-paths   ["env/dev"]
                    :resource-paths ["resources"]
                    :cljsbuild      {:builds [{:id           "dev"
                                               :source-paths ["src" "test"]
                                               :figwheel     {:on-jsload cljs-iota.run-tests/run-all-tests}
                                               :compiler     {:main                 "cljs-iota.run-tests"
                                                              :output-to            "resources/public/js/compiled/app.js"
                                                              :output-dir           "resources/public/js/compiled/out"
                                                              :asset-path           "/js/compiled/out"
                                                              :source-map-timestamp true
                                                              :optimizations        :none}}]}}})
