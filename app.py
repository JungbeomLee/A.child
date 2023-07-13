import requests
from flask import Flask, request, jsonify
from dotenv import load_dotenv
from utils.img_preprocessing import catch_face, is_image
from utils.help_caching import make_cache_key
import os
import cv2
import tensorflow as tf
import numpy as np
from tensorflow.keras.models import load_model
from flask_caching import Cache

app = Flask(__name__)
app.config.from_mapping({"DEBUG": True, "CACHE_TYPE": "simple", "CACHE_DEFAULT_TIMEOUT": 300})
test_model = load_model('models/model_siamese_neural_network.h5')
cache = Cache(app)

load_dotenv()

ESNTLID = os.getenv("ESNTLID")
AUTHKEY = os.getenv("AUTHKEY")

@app.route('/api/find', methods=['GET'])
@cache.cached(timeout=3600, key_prefix=make_cache_key)
def search_missing_person():
    name = request.args.get('name', default="", type=str)
    age = request.args.get('age', default=None, type=int)

    if name is not None or age is not None :
        res = requests.post("https://www.safe182.go.kr/api/lcm/findChildList.do", params={"esntlId" : ESNTLID, "authKey" : AUTHKEY, "rowSize" : "100", "nm" : name, "age1" : age, "age2": age}) 
        return res.json()
    else:
        return jsonify({"error" : "no input value"})
    
@app.route('/api/use_ai', methods=['POST'])
def use_ai():
    try:
        image = request.files['image']
        image2 = request.files['image2'] 

        image.save('static/images/img_01.jpg')
        image2.save('static/images/img_02.jpg')

        if is_image(image,image2):
            return jsonify({"error" : "you are not input file or file is not img"})
        
        image = cv2.imread('static/images/img_01.jpg')
        image2 = cv2.imread('static/images/img_02.jpg')
        
        image = catch_face(image)
        image2 = catch_face(image2)

        if image is None or image2 is None:
            return jsonify({"error" : "Error: face detection failed"}) 

        image = np.expand_dims(image, axis=0)
        image2 = np.expand_dims(image2, axis=0)  
        
        result = round(test_model.predict([image, image2])[0][0] * 100)

        return jsonify({"ai_result" : result})
    except:
        return jsonify({"error" : "error"})
     

if __name__ == '__main__':
	app.run(debug=True)