(ns components
  (:require [coast]
            [clojure.string :as string]))


(defn nav [session]
  [:nav {:class "dt w-100 border-box pa3 ph5-ns hero-topo-bg"}
   [:a {:class "dtc v-mid white link dim w-25" :href (if (some? (:member/email session))
                                                      (coast/url-for :home/dashboard)
                                                      (coast/url-for :home/index))
                                               :title "Home"}
    [:img {:src "/favicon.png" :class "dib w2 h2 br-100" :alt "Coast on Clojure"}]
    [:span {:class "ml2 v-top mt2 dib white"} "Coast"]]
   [:div {:class "dtc v-mid w-75 tr"}
    [:a {:class "link dim white f6 f5-ns dib mr3 mr4-ns" :href (coast/url-for :post/index) :title "Posts"} "Posts"]
    [:a {:class "link dim white f6 f5-ns dib mr3 mr4-ns" :href (coast/url-for :home/docs) :title "Docs"} "Docs"]
    [:a {:class "link dim white f6 f5-ns dib mr3 mr4-ns" :href "https://twitter.com/coastonclojure" :title "Twitter"} "Twitter"]
    [:a {:class "link dim white f6 f5-ns dib" :href "https://github.com/coast-framework/coast" :title "Github"} "Github"]]])


(defn layout [{:keys [flash title session]} body & scripts]
  [:html
    [:head
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:link {:rel "icon" :type "image/png" :href "/favicon.png"}]
     [:title (if (string/blank? title)
               (str "Coast on Clojure | The easy clojure web framework")
               (str "Coast on Clojure | " title))]
     (coast/css "bundle.css")
     (coast/js "bundle.js")]
    [:body
     (when (some? flash)
      [:div {:class "bg-light-blue bg-blue pa3 tc"}
       flash])
     (nav session)
     body
     scripts]])


(defn doc-layout [request body]
  (layout request body
    [:script {:src "/js/highlight.pack.js"}]
    [:script
     "hljs.initHighlightingOnLoad();"]))


(defn link-to [url & body]
  [:a {:href url :class "f6 link underline blue"}
    body])


(defn button-to
  ([am m s]
   (let [data (select-keys m [:data-confirm])
         form (select-keys am [:action :_method :method :class])]
     (coast/form (merge {:class "dib ma0"} form)
       [:input (merge data {:class "input-reset pointer link underline bn f6 br2 ma0 pa0 dib blue bg-transparent"
                            :type "submit"
                            :value s})])))
  ([am s]
   (button-to am {} s)))


(defn container [& args]
  (let [[m body] (if (map? (first args))
                   [(first args) (rest args)]
                   [{} args])
        mw (or (:mw m) 8)
        bg (or (:bg m) "white")]
    [:div {:class (str "pa4 w-100 center mw" mw " bg-" bg)}
     [:div {:class "overflow-auto"}
       body]]))


(defn table [& body]
  [:table {:class "f6 w-100" :cellspacing 0}
   body])


(defn thead [& body]
  [:thead body])


(defn tbody [& body]
  [:tbody {:class "lh-copy"} body])


(defn tr [& body]
  [:tr {:class "stripe-dark"}
   body])


(defn th
  ([s]
   [:th {:class "fw6 tl pa3 bg-white"} s])
  ([]
   (th "")))


(defn td [& body]
  [:td {:class "pa3"} body])


(defn submit [value]
  [:input {:class "input-reset pointer dim db bn f6 br2 ph3 pv2 dib white bg-blue"
           :type "submit"
           :value value}])

(defn submit-lg [value]
  [:input {:class "input-reset pointer dim db bn f6 br2 ph4 pv3 dib white bg-blue"
           :type "submit"
           :value value}])

(defn submit-block [value]
  [:input {:class "input-reset pointer dim db bn w-100 f6 br2 ph4 pv3 dib white bg-blue"
           :type "submit"
           :name "submit"
           :value value}])

(defn dt [s]
  [:dt {:class "f6 b mt2"} s])


(defn dd [s]
  [:dd {:class "ml0"} s])


(defn dl [& body]
  [:dl body])


(defn form-for
  ([k body]
   (form-for k {} body))
  ([k m body]
   (form-for k m {} body))
  ([k m params body]
   (coast/form-for k m (merge params {:class "pa4"})
     [:div {:class "measure"}
      body])))


(defn label [m s]
  [:label (merge {:for s :class "f6 b db mb2"} m) s])


(defn input [m]
  [:input (merge {:class "input-reset outline-0 pa3 db w-100 bg-near-white bn br1 f4"} m)])


(defn textarea [m & body]
  [:textarea (merge {:class "input-reset outline-0 pa3 db w-100 bg-near-white bn br1 f4"} m)
    body])


(defn text-muted [s]
  [:div {:class "f6 tc gray"}
   s])


(defn el [k m]
  (fn [& body]
    [k m body]))


(->> [:mr1 :mr2 :mr3 :mr4 :mr5 :ml1 :ml2 :ml3 :ml4 :ml5 :fr :cf]
     (mapv name)
     (mapv #(coast/intern-var % (el :span {:class %}))))


(defn tc [& body]
  [:div {:class "tc"}
   body])


(defn hero [& body]
  [:div {:class "hero-topo-bg pv6"}
    body])


(defn error [s]
  (when (some? s)
    [:div {:class "f6 red"}
     s]))
