=== WebSockets Demo with Liferay and Node ===

This repo contains two subdirectories, each of which must be copied into the right spot in a Liferay Plugins SDK before it'll work. The "realtime-hook" is a Hook which listens for Activity events in Liferay and sends messages through a WebSocket endpoint (or directly to Node via HTTP).

The "realtime-portlet" is a Portlet which establishes a WebSocket with the corresponding Endpoint in the above hook, then when a message is received from the hook, it plots a heatmap value onto the map, based on the location of the user that generated the activity. If you get many activities, it will build up a heatmap of activity on your site.


