#!/usr/bin/python
# -*- coding: utf-8 -*-
from mod_python import apache
from mod_python import Session
import mod_python
import cgi
import time
import sha
import os.path as path
from mod_python import util

storedir = "/home/www/munin/_android/data/"

def handler(req):
	id = path.basename(req.uri)
	if req.method == "GET":
		form = mod_python.util.FieldStorage(req,keep_blank_values=1)
		if "test" in form:
			req.write("munin ok")
	if req.method == "POST":
		data = req.read()
		outfile = open(storedir+id, "w")
		outfile.write(data)
		outfile.close()
		return apache.OK
	return apache.OK