import requests
from flask import Flask, request, jsonify
from dotenv import load_dotenv
from utils.image_embeding import *
from utils.image_utils import *
from utils.help_caching import make_cache_key
import os
import cv2
import tensorflow as tf
import numpy as np
from tensorflow.keras.models import load_model
from flask_caching import Cache
import sqlite3

app = Flask(__name__)
app.config.from_mapping({"DEBUG": True, "CACHE_TYPE": "simple", "CACHE_DEFAULT_TIMEOUT": 300})
embeding = FaceEmbedder(model_path = 'models/facenet_model.h5')
cache = Cache(app)
conn = sqlite3.connect('database.db', check_same_thread=False)
cursor = conn.cursor()

load_dotenv()

ESNTLID = os.getenv("ESNTLID")
AUTHKEY = os.getenv("AUTHKEY")

@app.route('/api/find', methods=['GET'])
@cache.cached(timeout=3600, key_prefix=make_cache_key)
def search_missing_person():
    name = request.args.get('name', default="", type=str)
    age = request.args.get('age', default=None, type=int)

    if name is not None or age is not None :
        res = requests.post("https://www.safe182.go.kr/api/lcm/findChildList.do", params={"esntlId" : ESNTLID, "authKey" : AUTHKEY, "rowSize" : "100", "nm" : name, "age1" : age, "age2": age, }) 
        return res.json()
    else:
        return jsonify({"error" : "no input value"})
    
@app.route('/api/use_ai', methods=['POST'])
def use_ai():
    fixed_image = request.files['FixImage']
    id = request.form['id']

    distance_list = [] 

    fixed_image.save('static/images/fixed_image.jpg')

    if is_image(fixed_image):
        return jsonify({"error" : "you are not input file or file is not img"})
    
    fixed_image_embedded_face = embeding.get_embedded_face('static/images/fixed_image.jpg')

    cursor.execute("SELECT vector FROM AIvector;")
    all_embedded_face = cursor.fetchall()

    for i in all_embedded_face:
        DB_embedded_face = i[0]
        DB_embedded_face = np.array(DB_embedded_face[1:-1].split(), dtype=np.float32)
        distance_list.append(embeding._get_distance(fixed_image_embedded_face,DB_embedded_face))

    cursor.execute('INSERT INTO AIvector (session_id, img, vector) VALUES (?,?,?)', (id, read_image('static/images/fixed_image.jpg'), str(fixed_image_embedded_face)))
    conn.commit()

    similar_distance_list = embeding._get_most_similar_vactor(distance_list)
    distance_list = [float(x) for x in distance_list]
    similar_distance_list_index = []

    for i in similar_distance_list:
        similar_distance_list_index.append(i[0])
    
    cursor.execute('SELECT session_id FROM AIvector WHERE id IN(?,?,?,?,?)', similar_distance_list_index)
    similar_distance_uid_list = cursor.fetchall()

    return jsonify({'distance_list': distance_list, 'similar_distance_list' : similar_distance_list, 'similar_distance_uid' : similar_distance_uid_list})


if __name__ == '__main__':
	app.run(debug=True)