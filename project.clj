(defproject reagent-secretary-accountant-petrol-example "0.1.0-SNAPSHOT"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.6.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.89"]
                 [org.clojure/core.async "0.2.385"
                  :exclusions [org.clojure/tools.reader]]
                 [reagent "0.6.0-rc"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.7"]
                 [petrol "0.1.3"
                  :exclusions [bidi kibu/pushy com.cemerick/url]]]

  :plugins [[lein-figwheel "0.5.4-7"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:open-urls ["http://localhost:3449/"]}

                :compiler {:main rsape.core
                           :asset-path "/js/compiled/out"
                           :output-to "resources/public/js/compiled/rsape.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}]}

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler rsape.server/handler })

;; NB this last line :ring-handler is important because it's pointing figwheel at
;; the handler which returns index.html for any request except js and css.  If you're
;; not using figwheel then you have to find some other way to do that, eg nginx rule.
