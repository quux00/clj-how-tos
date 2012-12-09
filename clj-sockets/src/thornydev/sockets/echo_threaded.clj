(ns thornydev.sockets.echo-threaded
  (:require [thornydev.clj-sockets :as sk]
            [clojure.string :refer [upper-case]])
  (:import (java.util.concurrent Executors)
           (java.io BufferedReader PrintWriter)
           (java.net ServerSocket Socket InetAddress)
           (java.util.concurrent CountDownLatch)))


(def latch (CountDownLatch. 1))

(def port 8002)

(defn echo-server []
  ;; since socket-accept blocks until the client connects
  ;; we have to call countDown on the latch after initializing
  ;; the server socket
  (with-open [serv-sock (do (.countDown latch)
                            (sk/socket-server port))
              client-sock (sk/socket-accept serv-sock)
              sock-rdr (sk/socket-reader client-sock)
              sock-wtr (sk/socket-writer client-sock)]
    (println "Server started.")
    (flush)
    
    (binding [*in*  sock-rdr
              *out* sock-wtr]
      (loop [input (read-line)]
        (when-not (or (nil? input) (= ":quit" input))
          (println "From echo server:" (upper-case input))
          (recur (read-line)))))
    
    (println "Echo server closing down")))


(defn get-user-input []
  (print "Msg for the server: ")
  (flush)
  (read-line))

(defn echo-client []
  (with-open [echo-sock (Socket. (InetAddress/getLocalHost) port)
              ^PrintWriter    client-out (sk/socket-writer echo-sock)
              ^BufferedReader client-in  (sk/socket-reader echo-sock)]

    ;; read input from user on stdin
    (loop [input (get-user-input)]
      (when-not (nil? input)
        (.println client-out input)
        (flush)
        (println (.readLine client-in))
        (when-not (= ":quit" input)
          (recur (get-user-input))))))

  (println "Client closed down"))


(defn engage [& args]
  (let [service (Executors/newSingleThreadExecutor)]
    (.submit service echo-server)
    (.await latch)
    ;; wait a bit longer to make sure server is listening on the port
    (Thread/sleep 100)
    (echo-client)))
