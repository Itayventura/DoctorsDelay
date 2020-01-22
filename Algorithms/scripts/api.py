# -*- coding: utf-8 -*-
"""
Created on Tue Dec 24 21:16:50 2019

@author: shiranpilas
"""

from flask import Flask, request, jsonify
from sklearn.externals import joblib
import traceback
import sys
import pandas as pd
import numpy as np
from xml.dom import minidom 
import DoctorsDelay
import os.path
from os import path

global model
global model_columns
global model_flag

def getModelPath():
        relativeModelPath = r'../../config.xml'
        config_path = (os.path.abspath(relativeModelPath))
        xmlFileParser = minidom.parse(config_path)
        entries = xmlFileParser.getElementsByTagName('entry')
        modelPath = str(entries[0].firstChild.data).replace('\\','/')
        return modelPath
        
model_path = getModelPath()


# API definition
app = Flask(__name__)

@app.route('/predict', methods=['POST']) # Your API endpoint URL would consist /predict

def predict():
    global model
    global model_columns
    global model_flag
    if model_flag == 1:
        try:
            json_ = request.json
            print ("Json given", json_)
            
            query = pd.get_dummies(pd.DataFrame(json_))
            query = query.reindex(columns=model_columns, fill_value=0)

            #print(query)

            print ("going to predict")
            prediction = list(model.predict(query))

            return jsonify({'prediction': prediction})

        except:
            return jsonify({'trace': traceback.format_exc()})
    else:
        print ('Train the model first')
        return ('No model here to use')


@app.route('/buildModel', methods=['POST']) # Your API endpoint URL would consist /buildModel

def buildModel():
    global model
    global model_columns
    global model_flag

    print("Build Model. Please wait for few minutes")
    accuracy = DoctorsDelay.flow()
    
    model = joblib.load(model_path) # Load "model.pkl"
    model_columns = joblib.load("model_columns.pkl") # Load "model_columns.pkl"
    model_flag = 1
    print("model was built and loaded")

    try:
        return jsonify({'accuracy': accuracy*100})
    
    except:
        return jsonify({'trace': traceback.format_exc()})


def setupModel():
    global model
    global model_columns
    global model_flag

    if path.exists(model_path):

        model = joblib.load(model_path) # Load "model.pkl"
        print ('Model loaded')

        model_columns = joblib.load("model_columns.pkl") # Load "model_columns.pkl"
        print ('Model columns loaded')

        model_flag = 1
    else:
        model_flag = 0


if __name__ == '__main__':
    try:
        port = int(sys.argv[1]) # This is for a command-line input
    except:
        port = 12345 # If you don't provide any port the port will be set to 12345
        
    print ("Running api.py on port:", port)
    setupModel()
    app.run(port=port, debug=True)
    
    
