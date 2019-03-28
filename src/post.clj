(ns post
  (:require [coast]
            [markdown.core :as markdown]
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


(defn view [request]
  (let [id (-> request :params :post-id)
        post (coast/fetch :post id)]
    (container {:mw 7}
      [:h2 {:class "f1-l f-subheadline-l f2"}
        (:post/title post)]
      [:div {:class "content"}
       (coast/raw
         (markdown/md-to-html-string (:post/body post)))])))


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
     [:span.mr3]
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


(defn create [{:keys [member params] :as request}]
  (let [params (if (coast/xhr? request)
                 params
                 (merge params {:post/published-at (coast/now)}))
        [post errors] (-> (coast/validate params [[:required [:post/title :post/body]]])
                          (merge {:post/member (:member/id member)})
                          (select-keys [:post/member :post/body :post/published-at :post/title])
                          (coast/insert)
                          (coast/rescue))]
    (if (nil? errors)
      (if (coast/xhr? request)
        (coast/ok
         {:form-params (coast/action-for ::change post)
          :url (coast/url-for ::edit post)}
         :json)
        (coast/redirect-to :home/dashboard))
      (build (merge request errors)))))


(defn edit [request]
  (let [post (coast/fetch :post (-> request :params :post-id))]
    (container {:mw 7}
      [:div.cf
       [:div.fl
         [:div {:id "status" :class "f6 gray mb2"} (coast/raw "Saved")]]
       [:div.fr
         [:a {:class "blue pointer dim mr3" :id "edit"} "Edit"]
         [:a {:class "gray pointer dim" :id "preview"} "Preview"]]]

      [:div {:id "preview-container"}]
      [:div {:id "form-container"}
        (form (coast/action-for ::change post) {:params post})])))


(defn change [request]
  (let [post (coast/fetch :post (-> request :params :post-id))
        post (if (coast/xhr? request)
               post
               (if (some? (:post/published-at post))
                 post
                 (merge post {:post/published-at (coast/now)})))
        [_ errors] (-> (select-keys post [:post/id :post/member :post/published-at])
                       (merge (select-keys (:params request) [:post/title :post/body]))
                       (coast/validate [[:required [:post/id :post/member :post/body :post/title]]])
                       (select-keys [:post/id :post/member :post/body :post/published-at :post/title])
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
