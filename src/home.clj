(ns home
  (:require [coast]
            [clojure.string :as string]
            [components :refer [container hero]]
            [markdown.core :as markdown]))


(defn index [request]
  [:div
   (hero
    [:div {:class "tc-l ph3"}
     [:h1 {:class "f2 f1-l fw3 white mb0 lh-title mb4"} "Clojure web development made easy"]
     [:a {:class "no-underline br2 shadow-4 grow dib v-mid bg-green white ba b--green ph4 pv3 mb3 br1" :href (coast/url-for ::docs)} "Get Started with Coast"]])

   [:div {:class "pv6 bg-white"}
    [:div {:class "tc-l ph3"}
     [:h2 {:class "f2 f1-l fw3 near-black mb0 lh-title mb4"}
      "The missing clojure web framework"]
     [:p {:class "lh-copy measure center-l"}
      "Making a modern web app can be crazy. Between tracking down up-to-date, secure dependencies and your own application code, things can get out of control quickly.
       Coast on Clojure makes it much easier and more fun.
       It includes everything you need to make great web applications."]]]

   (hero
    [:div {:class "cf ph3 mw8 center white"}
     [:div {:class "fl w-100 w-third-ns ph2"}
      [:h3 {:class "f3 f2-l fw3 mb0 lh-title"}
        "⚡️ Fast"]
      [:p {:class "lh-copy measure"}
        "Coast was made to take your ideas from thought to completion to as fast as possible"]]

     [:div {:class "fl w-100 w-third-ns ph2"}
      [:h3 {:class "f3 f2-l fw3 mb0 lh-title"}
        "👮‍♂️ Secure"]
      [:p {:class "lh-copy measure"}
        "Coast takes security seriously and helps you avoid many common security mistakes"]]

     [:div {:class "fl w-100 w-third-ns ph2"}
      [:h3 {:class "f3 f2-l fw3 mb0 lh-title"}
        "🎉 Fun"]
      [:p {:class "lh-copy measure"}
        "Make web development fun again with consistent syntax and lightning fast feedback via the REPL"]]])])


(defn heading-anchors [text state]
  (if (true? (:inline-heading state))
    (let [[_ heading] (re-find #">(.*)<" text)
          anchor (-> (string/replace heading #"\s" "-")
                     (string/lower-case))
          anchor (str "<h$1 id=\"user-content-" anchor "\">")]
      [(string/replace text #"<h([0-9])>" anchor) state])
    [text state]))


(defn tip [text state]
  (if (string/starts-with? text "<p>TIP:")
    (let [s (string/replace text #"<p>" "<p class=\"tip\">")]
      [s state])
    [text state]))


(defn note [text state]
  (if (string/starts-with? text "<p>NOTE:")
    (let [s (string/replace text #"<p>" "<p class=\"note\">")]
      [s state])
    [text state]))


(defn doc [request]
  (let [filename (get-in request [:params :doc])]
    [:div {:class "grid near-white"}
     [:div {:class "pa4 near-white sidebar-container"}
      [:div {:class "fr-l sidebar"}
       (coast/raw
         (markdown/md-to-html-string (slurp "markdown/readme.md") :heading-anchors true))]]
     [:div {:class "ph4 bg-white content"}
      (coast/raw
        (markdown/md-to-html-string
         (slurp (str "markdown/" filename ".md"))
         :custom-transformers [heading-anchors tip note]))]]))


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
