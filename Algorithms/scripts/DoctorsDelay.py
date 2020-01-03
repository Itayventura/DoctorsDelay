# -*- coding: utf-8 -*-
"""
Created on Thu Dec 19 19:39:04 2019

@author: shiranpilas
"""

import pandas as pd
import numpy as np
from sklearn.metrics import classification_report, confusion_matrix
from sklearn.externals import joblib


path = r"doctorsReports.csv"
model_path = r"model.pkl"
model_columns_path = r"model_columns.pkl"

def one_hot_creation(data, categorical_variables,numeric_variables,label_variables):
    #Creating one-hot columns for all categorial variables.
    oh_data = data['Doctor\'s name']
    for variable in categorical_variables:
        #One-hot for training set.
        onehot_data_col = pd.get_dummies(data[variable],prefix=variable)
        oh_data = pd.concat([oh_data,onehot_data_col],axis=1)
    
    data_temp = pd.concat([oh_data, data[numeric_variables]], axis=1)
    data = pd.concat([data_temp, data[label_variables]], axis=1)
    return data


def spliting_data(data,is_exist_feedbacks):
    
    if(is_exist_feedbacks == 1):
        #extract feedback reports rows. 
        feedback_data = data[data['TypeReport_feedback'] == 1]
        
        # Get names of indexes for which column TypeReport has value 'feedback'.
        indexNames = data[ data['TypeReport_feedback'] == 1 ].index
        
        #delet feddbacks from data set.
        data = data.drop(indexNames,axis = 0)
        
        #Input.
        columns_for_estimation_feedback = feedback_data.drop('delayCategorial', axis = 1)
        
        #Output
        delay_estimation_feedback = feedback_data['delayCategorial']


    #Input.
    columns_for_estimation = data.drop('delayCategorial', axis = 1)
    
    #Output.
    delay_estimation = data['delayCategorial']

    #Spliting data to training and testing sets.
    from sklearn.model_selection import train_test_split
    X_train, X_test, y_train, y_test = train_test_split(columns_for_estimation, delay_estimation, test_size = 0.20)
    
    if (is_exist_feedbacks == 1):
        
        X_train_f, X_test_f, y_train_f, y_test_f = train_test_split(columns_for_estimation_feedback, delay_estimation_feedback, test_size = 0.0)
        
        #insert feedback reports rows in training data. 
        X_train = pd.concat([X_train,X_train_f ], axis=0)
        X_test = pd.concat([X_test,X_test_f ], axis=0)
        y_train = pd.concat([y_train,y_train_f ], axis=0)
        y_test = pd.concat([y_test,y_test_f ], axis=0)
    
    return X_train,X_test,y_train,y_test


def calc_our_score(matrix):

    upper_sum = np.triu(matrix).sum()-np.trace(matrix)
    lower_sum = np.tril(matrix).sum()-np.trace(matrix)
    diagonal_sum = sum([matrix[i][i] for i in range(0,len(matrix))])
    our_score = diagonal_sum*15 + upper_sum - lower_sum*5

    return our_score


def SVM_linear(X_train,X_test,y_train,y_test):

    #Build SVM linear classifier.
    from sklearn.svm import SVC
    svm_linear = SVC(kernel='linear',decision_function_shape='ovr', break_ties=True)
        
    #Learn and build the model.
    svm_linear.fit(X_train, y_train)
        
    #Predict and print accuracy.
    y_pred = svm_linear.predict(X_test)
        
    training_set_accuracy_linear_svm = svm_linear.score(X_train, y_train)
    testing_set_accuracy_linear_svm = svm_linear.score(X_test, y_test)
        
    #Print classifier performance
    print('Accuracy of SVM linear classifier on training set: {:.2f}'.format(training_set_accuracy_linear_svm))
    print('Accuracy of SVM linear classifier on test set: {:.2f}'.format(testing_set_accuracy_linear_svm ))
    print()

    array = confusion_matrix(y_test,y_pred)
    print(array)
    print(classification_report(y_test,y_pred))
    print()

    our_score_linear_svm = calc_our_score(array)
    print(our_score_linear_svm)

    return svm_linear,testing_set_accuracy_linear_svm,our_score_linear_svm


def SVM_rbf(X_train,X_test,y_train,y_test):

    from sklearn.svm import SVC
    
    #Build SVM rbf classifier.
    svm_rbf = SVC(kernel='rbf', random_state=0, gamma= 0.01, C=100)

    #Learn and build the model.
    svm_rbf.fit(X_train, y_train)
        
    #Predict and print accuracy.
    y_pred = svm_rbf.predict(X_test)
        
    training_set_accuracy_rbf_svm = svm_rbf.score(X_train, y_train)
    testing_set_accuracy_rbf_svm = svm_rbf.score(X_test, y_test)
        
    #Print classifier performance
    print('Accuracy of SVM rbf classifier on training set: {:.2f}'.format(training_set_accuracy_rbf_svm))
    print('Accuracy of SVM rbf classifier on test set: {:.2f}'.format(testing_set_accuracy_rbf_svm ))
    print()

    array = confusion_matrix(y_test,y_pred)
    print(array)
    print(classification_report(y_test,y_pred))
    print()

    our_score_rbf_svm = calc_our_score(array)
    print(our_score_rbf_svm)

    return svm_rbf,testing_set_accuracy_rbf_svm,our_score_rbf_svm


def SVM_poly(X_train,X_test,y_train,y_test):

    from sklearn.svm import SVC
            
    #Build SVM poly classifier.
    svm_poly = SVC(kernel='poly',gamma = 'scale',degree = 3)
        
    #Learn and build the model.
    svm_poly.fit(X_train, y_train)
        
    #Predict and print accuracy.
    y_pred = svm_poly.predict(X_test)
        
    training_set_accuracy_poly_svm = svm_poly.score(X_train, y_train)
    testing_set_accuracy_poly_svm = svm_poly.score(X_test, y_test)
        
    #Print classifier performance
    print('Accuracy of SVM poly classifier on training set: {:.2f}'.format(training_set_accuracy_poly_svm))
    print('Accuracy of SVM poly classifier on test set: {:.2f}'.format(testing_set_accuracy_poly_svm ))
    print()
        
    print(matrix = confusion_matrix(y_test,y_pred))
    print(classification_report(y_test,y_pred))
    print()

    our_score_poly_svm = calc_our_score(matrix)
    print(our_score_poly_svm)

    return svm_poly,testing_set_accuracy_poly_svm,our_score_poly_svm


def random_forest(X_train,X_test,y_train,y_test):
            
    from sklearn.ensemble import RandomForestClassifier
    
    #Build RandomForest classifier.
    random_forest_classifier = RandomForestClassifier(n_jobs=1,max_depth = 3, random_state=0,n_estimators=500)
    
    #Learn and build the model.
    random_forest_classifier.fit(X_train, y_train)
    
    #Predict and print accuracy.
    y_pred = random_forest_classifier.predict(X_test)
    
    training_set_accuracy_random_forest = random_forest_classifier.score(X_train, y_train)
    testing_set_accuracy_random_forest = random_forest_classifier.score(X_test, y_test)
    
    print('Accuracy of Random Forest classifier on training set: {:.2f}'.format(training_set_accuracy_random_forest))
    print('Accuracy of Random Forest classifier on test set: {:.2f}'.format(testing_set_accuracy_random_forest ))
    print()

    array = confusion_matrix(y_test,y_pred)
    print(array)
    print(classification_report(y_test,y_pred))
    print()

    our_score_random_forest = calc_our_score(array)
    print(our_score_random_forest)
    

    return random_forest_classifier,testing_set_accuracy_random_forest, our_score_random_forest


def search_and_build_model(X_train,X_test,y_train,y_test, number_of_possible_predicted_class):

    if(number_of_possible_predicted_class > 1):
        
        svm_linear,testing_set_accuracy_linear_svm,our_score_linear_svm = SVM_linear(X_train,X_test,y_train,y_test)
        
        svm_rbf,testing_set_accuracy_rbf_svm,our_score_rbf_svm = SVM_rbf(X_train,X_test,y_train,y_test)

        #svm_poly,testing_set_accuracy_poly_svm,our_score_poly_svm = SVM_poly(X_train,X_test,y_train,y_test)
                
    random_forest_classifier,testing_set_accuracy_random_forest,our_score_random_forest = random_forest(X_train,X_test,y_train,y_test)


    max_score_our = max(our_score_linear_svm,our_score_rbf_svm,our_score_random_forest)

    if(our_score_linear_svm == max_score_our):
        classifier = svm_linear
        testing_set_accuracy_model = testing_set_accuracy_linear_svm
        
    elif(our_score_rbf_svm == max_score_our):
        classifier = svm_rbf
        testing_set_accuracy_model = testing_set_accuracy_rbf_svm
        
    else:
        classifier = random_forest_classifier
        testing_set_accuracy_model = testing_set_accuracy_random_forest
        
    return classifier, testing_set_accuracy_model
        

def save_model(model):
    joblib.dump(model,model_path)
    print("model saved")

    
def flow():
    data = pd.read_csv(path, header=None)
    print("Reading csv file done")
    is_exist_feedbacks = 0
    number_of_possible_predicted_class = 0
    data = data.drop(data.index[0])
    
    data.columns = ['Doctor\'s name', 'delay' ,'TypeReport','month', 'day', 'hour', 'minutes' ]
    
    #Specifying data types.
    categorical_variables = ['Doctor\'s name','TypeReport','month', 'day']
    numeric_variables = ['delay','hour','minutes']
    label_variables = ['delayCategorial']
    
    if ('feedback' in data['TypeReport']):
        is_exist_feedbacks = 1
    
    for variable in numeric_variables:
        data[variable] = data[variable].astype(np.int64)
    
    #Creating new 'delayCategorial' column based on 'delay' column.
    data['delayCategorial'] = data['delay']
    data.loc[(data['delay'] <= 15) & (data['delay'] >= 0), 'delayCategorial'] = "S"
    data.loc[(data['delay'] <= 30) & (data['delay'] >= 16), 'delayCategorial'] = "M"
    data.loc[data['delay'] >= 31, 'delayCategorial'] = "L"
    
    number_of_possible_predicted_class = data['delayCategorial'].nunique()
    print(number_of_possible_predicted_class)

    #Random examples in data set.
    data = data.sample(frac = 1)
    print("before calling one_hot_creation")
    
    #Creating one-hot columns for all categorial variables.
    data = one_hot_creation(data,categorical_variables,numeric_variables,label_variables)
      
    #Drop unnecessary for training columns.
    data = data.drop('Doctor\'s name',axis = 1)
    
    #Ignore delay and use delayCategorial instead.
    data = data.drop('delay',axis = 1)
    
    #spliting data consider feedback reports in training data.
    X_train,X_test,y_train,y_test = spliting_data(data,is_exist_feedbacks)
    
    #save lables of x_train culomns for future prediction.
    model_columns = list(X_train.columns)
    joblib.dump(model_columns, 'model_columns.pkl')
    ['model_columns.pkl']
    
    #build appropriate model.
    model,accuracy = search_and_build_model(X_train,X_test,y_train,y_test,number_of_possible_predicted_class)
    
    #save model for future use.
    save_model(model)

    return accuracy
