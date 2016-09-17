(ns rsape.core
  (:require [cljs.core.async :as async]
            [reagent.core    :as r]
            [secretary.core  :as secretary :refer-macros [defroute]]
            [accountant.core :as accountant]
            [petrol.core     :as petrol]))

;;;; pages

(defn header []
  [:div [:h1 "Routing with Secretary, Accountant & Petrol"]
   [:ul
    [:li>a {:href "/"}      "home"]      ;; These are <a> tags but accountant notices that
    [:li>a {:href "/one"}   "One!"]      ;; they correspond to secretary routes and uses the
    [:li>a {:href "/two"}   "Two!"]      ;; pushState API rather than reloading the page
    [:li>a {:href "/three"} "Three!"]    ;;        (NB no #/ in the URL)
    [:li>a {:href "/404"} "(doesn't exist)"]]])

(defn welcome-page   [ui-channel app] [:div "WELCOME!"])
(defn not-found-page [ui-channel app] [:div "not found :("])
(defn number-page    [ui-channel app] [:div (str "page " (:number app))])

(defn the-app [ui-channel app]
  [:div [header]
   [(:current-page app) ui-channel app]])



;;;; nav channel & messages

(def nav-channel (async/chan))

(defrecord WelcomePage  [])
(defrecord NotFoundPage [])
(defrecord NumberPage   [num])

(extend-protocol petrol/Message
  WelcomePage  (process-message [_ app] (assoc app :current-page welcome-page))
  NotFoundPage (process-message [_ app] (assoc app :current-page not-found-page))
  NumberPage   (process-message [m app] (assoc app :number (:num m)
                                                   :current-page number-page)))


;;;; routes

(defroute "/"      [] (async/put! nav-channel (->WelcomePage)))
(defroute "/one"   [] (async/put! nav-channel (->NumberPage 1)))
(defroute "/two"   [] (async/put! nav-channel (->NumberPage 2)))
(defroute "/three" [] (async/put! nav-channel (->NumberPage 3)))
(defroute "/*"     [] (async/put! nav-channel (->NotFoundPage))) 
  
(accountant/configure-navigation! {:nav-handler secretary/dispatch!
                                   :path-exists? secretary/locate-route})  
(accountant/dispatch-current!)



;; app bootstrapping

(def !app (r/atom {:current-page (fn [& _] [:div "loading"])
                   :number 0}))

(defn render-fn [ui-channel app]
  (r/render-component [the-app ui-channel app] (.getElementById js/document "app")))

(defn main []
  (enable-console-print!)
  (petrol/start-message-loop! !app render-fn [nav-channel]))

(main)

