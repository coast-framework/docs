(ns home
  (:require [coast]
            [clojure.string :as string]
            [components :refer [container]]
            [markdown.core :as markdown]))


(defn index [request]
  (container
   [:div {:class "tc-l mt4 mt5-m mt6-l ph3"}
    [:h1 {:class "f2 f1-l fw3 mb0 lh-title mb4"} "Clojure web development made easy"]
    [:a {:class "f6 no-underline shadow-4 grow dib v-mid bg-blue white ba b--blue ph4 pv3 mb3 br1" :href (coast/url-for ::docs)} "Get Started with Coast"]]))


(defn heading-anchors [text state]
  (if (true? (:inline-heading state))
    (let [[_ heading] (re-find #">(.*)<" text)
          anchor (-> (string/replace heading #"\s" "-")
                     (string/lower-case))
          anchor (str "<h$1 id=\"user-content-" anchor "\">")]
      [(string/replace text #"<h([0-9])>" anchor) state])
    [text state]))


(defn doc [request]
  (let [filename (get-in request [:params :doc])]
    (container
      [:div {:class "cf"}
       [:div {:class "fl w-100 w-25-ns pa2"}
        [:div {:class "sidebar"}
         (coast/raw
           (markdown/md-to-html-string (slurp "markdown/README.md") :heading-anchors true))]]
       [:div {:class "fl w-100 w-75-ns pa2"}
        [:div {:class "content"}
         (coast/raw
           (markdown/md-to-html-string
            (slurp (str "markdown/" filename ".md"))
            :custom-transformers [heading-anchors]))]]])))


(defn docs [request]
  (doc {:params {:doc "installation"}}))


(defn screencast [request]
  [:div "screencast!"])


(defn not-found [request]
  (coast/not-found
    [:html
     [:head
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      (coast/css "bundle.css")
      (coast/js "bundle.js")]
     [:body
      [:h1 "Couldn't find what you were looking for"]]]))


(defn server-error [request]
  (coast/server-error
    [:html
      [:head
       [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
       (coast/css "bundle.css")
       (coast/js "bundle.js")]
      [:body
       [:h1 "Something went wrong!"]]]))
