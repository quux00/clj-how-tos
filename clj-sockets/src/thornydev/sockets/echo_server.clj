(ns thornydev.sockets.echo-server
  (:require [server.socket :refer [create-server close-server]]
            [clojure.string :as str])
  (:import (java.io BufferedReader IOException InputStreamReader PrintWriter)
           (java.net ServerSocket Socket)
           (java.util.concurrent CountDownLatch)))

(def port 8000)
  
;; use CountDownLatch to allow the socket-server
;; thread to notify other observers (the main thread)
;; when it has finished
(def latch (CountDownLatch. 1))

(defn echo-server [in out]
  (binding [*in* (BufferedReader. (InputStreamReader. in))
            *out* (PrintWriter. out)]
    (loop [input (read-line)]
      (when-not (or (nil? input) (= ":quit" input))
        (println "From echo server:" (clojure.string/upper-case input))
        (flush)
        (recur (read-line))))
    )
  (.countDown latch))


;; (defn echo-server2 [in out]
;;   (let [rdr (BufferedReader. (InputStreamReader. in))
;;         wtr (PrintWriter. out)]
;;     (loop [input (.readLine rdr)]
;;       (when-not (or (nil? input) (= ":quit" input))
;;         (.println wtr (str "From echo server:" (str/upper-case input)))
;;         (.flush wtr)
;;         (recur (.readLine rdr))))
;;     )
;;   (.countDown latch))


(defn do-server []
  (let [socket-server (create-server port echo-server)]
    (.await latch)
    ;; clean up resources when the server is done
    (close-server socket-server)))

