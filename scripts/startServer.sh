#!/bin/bash
 sudo /etc/rc.d/init.d/nginx start
 workon server
 gunicorn server:app -b localhost:8000 &