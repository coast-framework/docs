(ns post
  (:require [coast]
            [helpers]
            [markdown.core :as markdown]
            [clojure.string :as string]
            [components :refer [submit-block container tc link-to table thead tbody td th tr button-to text-muted mr2 dl dd dt submit input label textarea]]))


(defn preview [request]
  (let [{:keys [title body]} (:params request)]
    (coast/ok
      [:div
       [:h2 {:class "f1-l f-subheadline-l f2"} title]
       [:div {:class "content"}
        (coast/raw
          (markdown/md-to-html-string body))]]
      :html)))


(defn index [request]
  (let [posts (coast/q '[:select *
                         :from post
                         :where ["published_at is not null"]
                         :order published-at desc]
                       {:published-at nil})]
    (container {:mw 7}
      [:div {:class "content"}
        (for [{:post/keys [title body published-at] :as post} posts]
          [:div {:class "mb5"}
           [:time {:class "f6 gray mb1 dib"} (coast/strftime
                                              (coast/datetime published-at "US/Mountain")
                                              "MMMM dd, YYYY")]
           [:h2 {:class "pa0 f2-l f-subheadline-l f3 ma0" :style "padding-top: 0"} title]
           [:p {:class "pb0 mb1"} (helpers/ellipsis body 150)]
           [:a {:href (coast/url-for :post/view post)
                :class "underline blue"}
            "Read More"]])])))



(defn view [request]
  (let [slug (-> request :params :post-slug)
        post (coast/pluck '[:select *
                            :from post
                            :where [slug ?slug]
                                   ["published_at is not null"]]
                           {:slug slug})
        {:post/keys [published-at title body]} post]
    (if (nil? post)
      (coast/raise {:not-found true})
      (container {:mw 7}
        [:time {:class "f6 gray mb1 dib"} (coast/strftime
                                           (coast/datetime published-at "US/Mountain")
                                           "MMMM dd, YYYY")]
        [:h2 {:class "pa0 ma0 f1-l f-subheadline-l f2"} title]
        [:div {:class "content"}
         (coast/raw
           (markdown/md-to-html-string body))]))))


(defn errors [m]
  [:div {:class "bg-red white pa2 mb4 br1"}
   [:h2 {:class "f4 f-subheadline"} "Errors Detected"]
   [:dl
    (for [[k v] m]
      [:div {:class "mb3"}
       (dt (str k))
       (dd v)])]])


(defn form [form-params request]
  [:div
   (coast/form form-params
     (when (some? (:errors request))
      (errors (:errors request)))

     (input {:type "text" :placeholder "Title" :name "post/title" :value (-> request :params :post/title)})

     [:div.mb3]
     (textarea {:placeholder "Body" :name "post/body"
                :style "height: calc(100% - 340px)"}
       (-> request :params :post/body))

     [:div.mb3]
     (submit-block "Publish"))])


(defn build [request]
  (container {:mw 7}
    [:div.cf
     [:div.fl
       [:div {:id "status" :class "f6 gray mb2"} (coast/raw "Unsaved")]]
     [:div.fr
       [:a {:id "edit" :class "blue pointer mr3"} "Edit"]
       [:a {:id "preview" :class "gray pointer"} "Preview"]]]

    (container {:mw 7}
      [:div {:id "preview-container"}])
    [:div {:id "form-container"}
     (form (coast/action-for ::create) request)]))


(defn slug [s]
  (str (-> (.toLowerCase s)
           (string/replace #"\s+" "-")
           (string/replace #"[^\w\-]+" "")
           (string/replace #"\-\-+" "-")
           (string/replace #"^-+" "")
           (string/replace #"-+$" ""))
       "-" (last (string/split (str (coast/uuid)) #"-"))))


(defn create [{:keys [member params] :as request}]
  (let [params (if (coast/xhr? request)
                 params
                 (assoc params :post/published-at (coast/now)))
        params (assoc params :post/slug (slug (:post/title (:params request))))
        [post errors] (-> (merge params {:post/member (:member/id member)})
                          (coast/validate [[:required [:post/member]]])
                          (select-keys [:post/member :post/body :post/slug :post/published-at :post/title])
                          (coast/insert)
                          (coast/rescue))]
    (if (nil? errors)
      (if (coast/xhr? request)
        (coast/ok
         {:form-params (coast/action-for ::change post)
          :url (coast/url-for ::edit post)}
         :json)
        (coast/redirect-to :home/dashboard))
      (if (coast/xhr? request)
        (coast/server-error
         (form (coast/action-for ::create) (merge request errors)))
        (build (merge request errors))))))


(defn edit [request]
  (let [post (coast/fetch :post (-> request :params :post-id))]
    (container {:mw 7}
      [:div.cf
       [:div.fl
         [:div {:id "status" :class "f6 gray mb2"} (coast/raw "Saved")]]
       [:div.fr
         [:a {:class "blue pointer dim mr3" :id "edit"} "Edit"]
         [:a {:class "gray pointer dim mr3" :id "preview"} "Preview"]
         (coast/form (merge (coast/action-for ::change post) {:class "dib ma0"})
           [:input {:class "input-reset bn bg-transparent gray pointer dim"
                    :type "submit"
                    :name "submit"
                    :value "Un-publish"}])]]

      [:div {:id "preview-container"}]
      [:div {:id "form-container"}
        (form (coast/action-for ::change post) {:params post})])))


(defn change [{:keys [params] :as request}]
  (let [post (coast/fetch :post (:post-id params))
        post (if (some? (:post/slug post))
               post
               (assoc post :post/slug (slug (:post/title params))))
        post (condp = (:submit params)
               "Publish" (if (some? (:post/published-at post))
                           post
                           (assoc post :post/published-at (coast/now)))
               "Un-publish" (assoc post :post/published-at nil)
               post)
        [_ errors] (-> (select-keys post [:post/id :post/member :post/slug :post/published-at])
                       (merge (select-keys params [:post/title :post/body]))
                       (coast/validate [[:required [:post/id :post/member]]])
                       (select-keys [:post/id :post/member :post/slug :post/body :post/published-at :post/title])
                       (coast/update)
                       (coast/rescue))]
    (if (coast/xhr? request)
      (coast/ok {} :json)
      (if (nil? errors)
        (coast/redirect-to :home/dashboard)
        (edit (merge request errors))))))


(defn delete [request]
  (let [[_ errors] (-> (coast/fetch :post (-> request :params :post-id))
                       (coast/delete)
                       (coast/rescue))]
    (if (nil? errors)
      (coast/redirect-to :home/dashboard)
      (-> (coast/redirect-to :home/dashboard)
          (coast/flash "Something went wrong!")))))
