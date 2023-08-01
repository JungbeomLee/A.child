import cv2
import os
import mediapipe as mp
import numpy as np
from PIL import Image

def read_image(file_path):
    with open(file_path, 'rb') as file:
        return file.read()
            
def is_image(image):
    try:
        img1 = Image.open(image)
        img1.close()
        return False
    except:
        return True