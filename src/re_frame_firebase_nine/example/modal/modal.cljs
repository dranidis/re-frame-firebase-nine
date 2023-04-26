(ns re-frame-firebase-nine.example.modal.modal
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [re-frame-firebase-nine.example.subs :as subs]))


(defn modal-panel
  [{:keys [child size show?]}]
  [:div  {:class "modal-wrapper"}
   [:div {;; :class "modal-backdrop"
          ;; :on-click (fn [event]
          ;;             (dispatch [:modal-event {:show? (not show?)
          ;;                                      :child nil
          ;;                                      :size :default}])
          ;;             (.preventDefault event)
          ;;             (.stopPropagation event))
          }]
   [:div {:class "modal-child modal-dialog modal-dialog-centered"
          ;; :style {:width (case size
          ;;                  :extra-small "15%"
          ;;                  :small "30%"
          ;;                  :large "70%"
          ;;                  :extra-large "85%"
          ;;                  "50%")}
          }
    child]])

(defn modal []
  (let [modal (subscribe [::subs/modal])]
    (fn []
      [:div
       (when (:show? @modal)
         [modal-panel @modal])])))


(defn- close-modal []
  (dispatch [:modal-event {:show? false :child nil}]))


(defn hello []
  [:div
   {:style {:background-color "white"
            :padding          "16px"
            :border-radius    "6px"
            :text-align "center"}} "Hello modal!"])


(defn hello-bootstrap []
  [:div {:class "modal-content panel-danger"}
   [:div {:class "modal-header panel-heading"}

    [:h4 {:class "modal-title"} "Hello Bootstrap modal!"]]
   [:div {:class "modal-body"}
    [:div [:b (str "You can close me by clicking the Ok button, the X in the"
                   " top right corner, or by clicking on the backdrop.")]]]
   [:div {:class "modal-footer"}
    [:button {:type "button" :title "Cancel"
              :class "btn btn-default"
              :on-click #(close-modal)}
    ;;  [:i {:class "material-icons"} "close"]
     "Cancel"]
    [:button {:type "button" :title "Ok"
              :class "btn btn-default"
              :on-click (fn [x]
                          (println "OK")
                          (close-modal))} "Ok"]]])


(comment
  (close-modal))