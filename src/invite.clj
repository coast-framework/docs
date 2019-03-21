(ns invite
  (:require [coast]
            [components :refer [submit-block container tc link-to table thead tbody td th tr button-to text-muted mr2 dl dd dt submit input label]]))


(defn errors [m]
  [:div {:class "bg-red white pa2 mb4 br1"}
   [:h2 {:class "f4 f-subheadline"} "Errors Detected"]
   [:dl
    (for [[k v] m]
      [:div {:class "mb3"}
       (dt (str k))
       (dd v)])]])


(defn build [request]
  (container {:mw 6}
    (coast/form-for ::create
      (label {:for "invite/email"} "Your email")
      (input {:type "text" :placeholder "you@gmail.com" :name "invite/email" :value (-> request :params :invite/email)})
      (when (some? (:errors request))
        [:div {:class "f6 mb3"} (get-in request [:errors :invite/email])])

      [:div.mb3]

      (submit-block "Request an invite"))))


(defn create [{:keys [params] :as request}]
  (let [[_ errors] (-> (coast/validate params [[:required [:invite/email] "can't be blank"]
                                               [:email [:invite/email]]])
                       (select-keys [:invite/email])
                       (assoc :invite/code (str (coast/uuid)))
                       (coast/insert)
                       (coast/rescue))]
    (if (nil? errors)
      (-> (coast/redirect-to :home/index)
          (coast/flash "Invitation request received! Allow 24 hours for consideration. I apologize for the inconvenience."))
      (build (merge request errors)))))


(defn edit [request]
  (let [invite (coast/fetch :invite (-> request :params :invite-id))]
    (container {:mw 6}
      (when (some? (:errors request))
        (errors (:errors request)))

      (coast/form-for ::change invite
        (label {:for "invite/email"} "email")
        (input {:type "text" :name "invite/email" :value (:invite/email invite)})

        [:div.mb3]
        (label {:for "invite/approved-at"} "approved-at")
        (input {:type "text" :name "invite/approved-at" :value (:invite/approved-at invite)})

        [:div.mb3]
        (label {:for "invite/code"} "code")
        (input {:type "text" :name "invite/code" :value (:invite/code invite)})

        [:div.mb3]
        [:div.cf
          [:div.fr
            (link-to (coast/url-for :home/dashboard) "Cancel")
            [:span.mr3]
            (submit "Update invite")]]))))


(defn change [request]
  (let [invite (coast/fetch :invite (-> request :params :invite-id))
        [_ errors] (-> (select-keys invite [:invite/id])
                       (merge (:params request))
                       (coast/validate [[:required [:invite/id :invite/email :invite/approved-at :invite/code]]])
                       (select-keys [:invite/id :invite/email :invite/approved-at :invite/code])
                       (coast/update)
                       (coast/rescue))]
    (if (nil? errors)
      (coast/redirect-to :home/dashboard)
      (edit (merge request errors)))))


(defn delete [request]
  (let [[_ errors] (-> (coast/fetch :invite (-> request :params :invite-id))
                       (coast/delete)
                       (coast/rescue))]
    (if (nil? errors)
      (coast/redirect-to :home/dashboard)
      (-> (coast/redirect-to :home/dashboard)
          (coast/flash "Something went wrong!")))))


(defn approve [{:keys [params]}]
  (let [invite (coast/pluck '[:select *
                              :from invite
                              :where [id ?id
                                      approved-at ?approved-at]]
                            {:id (:id params)
                             :approved-at nil})]
    (if (nil? invite)
      (-> (coast/redirect-to :home/dashboard)
          (coast/flash "That invite either doesn't exist or was already approved"))
      (let [_ (-> (assoc invite :invite/approved-at (coast/now))
                  (select-keys [:invite/id :invite/approved-at])
                  (coast/update))]
        (-> (coast/redirect-to :home/dashboard)
            (coast/flash "Invite approved"))))))
