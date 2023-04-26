(ns re-frame-firebase-nine.example.core
  (:require [re-frame-firebase-nine.example.config :as config]
            [re-frame-firebase-nine.example.events :as events]
            [re-frame-firebase-nine.example.fb-config :refer [get-config]]
            [re-frame-firebase-nine.example.modal.modal :as modal]
            [re-frame-firebase-nine.example.subs]
            [re-frame-firebase-nine.example.views :as views]
            [re-frame-firebase-nine.fb-reframe :refer [connect-emulator
                                                       fb-reframe-config]]
            [re-frame.core :as re-frame]
            [reagent.dom :as rdom]))

(defn render-view [view]
 [:div
  [modal/modal] view])

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render (render-view [views/main-panel]) root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)

  (fb-reframe-config {:temp-path [:re-frame-firebase-nine-temp]
                      :firebase-config (get-config)})
  (connect-emulator)

  (mount-root))

(comment
  init)