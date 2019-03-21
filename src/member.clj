(ns member
  (:require [coast]
            [components :refer [container tc link-to table thead tbody td th tr button-to text-muted mr2 dl dd dt submit submit-lg input label mr3 fr cf submit-block error]]
            [buddy.hashers :as hashers]))


(defn errors [m]
  [:div {:class "bg-red white pa2 mb4 br1"}
   [:h2 {:class "f4 f-subheadline"} "Errors Detected"]
   [:dl
    (for [[k v] m]
      [:div {:class "mb3"}
       (dt (str k))
       (dd v)])]])


(defn build [{:keys [errors]}]
  (container {:mw 6}

    [:h1 {:class "f1-l f2 f-subheadline-l tc"}
     "Create an account"]

    (coast/form (coast/action-for ::create)
      (label {:for "member/password"} "Password")
      (input {:type "password" :name "member/password"})
      (error (:member/password errors))

      [:div.mb3]

      (label {:for "member/password-confirmation"} "Password confirmation")
      (input {:type "password" :name "member/password-confirmation"})
      (error (:member/password-confirmation errors))

      [:div.mb3]

      (label {:for "invite/code"} "Invitation code")
      (input {:type "text" :name "invite/code"})
      (error (:invite/code errors))

      [:div.mb3]

      [:div {:class "tc mb4"}
       (link-to (coast/url-for :invite/build) "Don't have an invite? Request one")]

      (tc
        (submit-block "Sign Up")))))


(defn create [{:keys [params] :as request}]
  (let [invite (coast/pluck '[:select *
                              :from invite
                              :where [code ?code]
                                     ["approved_at is not null"]]
                            {:code (get params :invite/code)})
        [m errors] (-> (coast/validate params [[:required [:member/password
                                                           :member/password-confirmation
                                                           :invite/code] "can't be blank"]
                                               [:equal [:member/password :member/password-confirmation] "needs to match"]])
                       (assoc :member/email (:invite/email invite))
                       (select-keys [:member/email :member/password])
                       (update :member/password str)
                       (update :member/password hashers/derive)
                       (coast/rescue))
        [_ errors] (if (nil? (:member/email m))
                    [nil (merge-with merge (or errors {:errors {}}) {:errors {:invite/code "There was a problem with that invitation code"}})]
                    [(coast/insert m) nil])]
    (if (nil? errors)
      (-> (coast/redirect-to :home/dashboard)
          (assoc :session {:member/email (:invite/email invite)}))
      (build (merge request errors)))))


(defn edit [request]
  (let [member (coast/fetch :member (-> request :params :member-id))]
    (container {:mw 6}
      (when (some? (:errors request))
        (errors (:errors request)))

      (coast/form-for ::change member
        (label {:for "member/first-name"} "first-name")
        (input {:type "text" :name "member/first-name" :value (:member/first-name member)})

        [:div.mb3]

        (label {:for "member/email"} "email")
        (input {:type "text" :name "member/email" :value (:member/email member)})

        [:div.mb3]

        (label {:for "member/last-name"} "last-name")
        (input {:type "text" :name "member/last-name" :value (:member/last-name member)})

        [:div.mb3]

        [:div.cf
         [:div.fr
          (link-to (coast/url-for :home/dashboard) "Cancel")
          [:span.mr3]
          (submit "Update member")]]))))


(defn change [request]
  (let [member (coast/fetch :member (-> request :params :member-id))
        [_ errors] (-> (select-keys member [:member/id])
                       (merge (:params request))
                       (coast/validate [[:required [:member/id :member/first-name :member/email :member/last-name :member/password]]])
                       (select-keys [:member/id :member/first-name :member/email :member/last-name :member/password])
                       (coast/update)
                       (coast/rescue))]
    (if (nil? errors)
      (coast/redirect-to ::index)
      (edit (merge request errors)))))


(defn delete [request]
  (let [[_ errors] (-> (coast/fetch :member (-> request :params :member-id))
                       (coast/delete)
                       (coast/rescue))]
    (if (nil? errors)
      (coast/redirect-to ::index)
      (-> (coast/redirect-to ::index)
          (coast/flash "Something went wrong!")))))
