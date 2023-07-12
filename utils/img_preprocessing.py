import cv2
import os
import mediapipe as mp
import numpy as np
from PIL import Image


def catch_face(img):
    mp_face_detection = mp.solutions.face_detection
    margin = 0.15
    with mp_face_detection.FaceDetection(model_selection=1, min_detection_confidence=0.5) as face_detection:
            img = np.array(img)
            face = face_detection.process(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
            if face.detections:
                for i, detection in enumerate(face.detections):
                    # Get bounding box coordinates
                    box = detection.location_data.relative_bounding_box
                    ih, iw, _ = img.shape
                    x, y, w, h = int(box.xmin * iw), int(box.ymin * ih), int(box.width * iw), int(box.height * ih)
                    
                    # Calculate margins for x, y, w, h
                    mx = int(w * margin)
                    my = int(h * margin)
                    
                    # Crop the face with margins
                    face_image = img[y+my:y+h-my, x+mx:x+w-mx]
                    if face_image.size != 0:
                        # Resize the image to 105x105
                        face_image = cv2.resize(face_image, (105, 105))
                        face_image = cv2.cvtColor(face_image, cv2.COLOR_BGR2GRAY)
                        return face_image
                    else :
                        return ValueError
            else :
                return print('cant find face')
            
def is_image(image,image2):
    try:
        img1 = Image.open(image)
        img1.close()
        img2 = Image.open(image2)
        img2.close()
        return False
    except:
        return True