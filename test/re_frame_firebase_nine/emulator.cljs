(ns re-frame-firebase-nine.emulator
  (:require [re-frame-firebase-nine.fb-reframe :refer [fb-reframe-config connect-emulator]]
            [re-frame-firebase-nine.example.fb-config :refer [get-config]]
            [re-frame-firebase-nine.firebase-database :refer [fb-set! fb-ref get-db]]))

(defn connect-fb-emulator-empty-db
  []
  (fb-reframe-config {:temp-path [:firebase-temp-storage]
                      :firebase-config (get-config)})

  (connect-emulator)
  (fb-set! (fb-ref (get-db)) nil))