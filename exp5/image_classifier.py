import sys
import numpy as np
from PIL import Image
from tflite_runtime.interpreter import Interpreter
labels = ['Airplane', 'Automobile', 'Bird', 'Cat', 'Deer', 'Dog', 'Frog', 
'Horse', 'Ship', 'Horse']  
tflite_model_file = './tflite_models/model_qat.tflite'          
interpreter = Interpreter(model_path=tflite_model_file)
interpreter.allocate_tensors()
input_index = interpreter.get_input_details()[0]['index']
output_index = interpreter.get_output_details()[0]['index']
Im = Image.open(sys.argv[1])
Im_resized = Im.resize((32, 32))
Im = np.asarray(Im_resized)
Im = Im/255
input_data = np.array(Im, dtype=np.float32)
input_data  =  input_data.reshape(1,  input_data.shape[0],    input_data.
shape[1], 3)
interpreter.set_tensor(input_index, input_data)
interpreter.invoke()
prediction = interpreter.get_tensor(output_index)
prediction = np.argmax(prediction)
print(labels[prediction])
