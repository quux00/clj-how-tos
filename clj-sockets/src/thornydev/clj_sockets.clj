(ns thornydev.clj-sockets
  (:import (java.io BufferedReader IOException
                    InputStreamReader PrintWriter)
           (java.net ServerSocket Socket InetAddress)))

(defn socket-server [port]
  (ServerSocket. port))

(defn socket-accept [^ServerSocket server-socket]
  (.accept server-socket))

(defn socket-reader [^Socket socket]
  (BufferedReader. (InputStreamReader. (.getInputStream socket))))

(defn socket-writer [^Socket socket]
  (PrintWriter. (.getOutputStream socket) true))
