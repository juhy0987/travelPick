import os
from dotenv import load_dotenv
from flask import Flask, request, jsonify

load_dotenv()

from lib import chroma, utils

app = Flask(__name__)

text_model_type="longformer"
text_collection_name = "text"
text_cluster_collection_name = "text_cluster"
image_model_type="clip"
image_collection_name = "image"
image_cluster_collection_name = "image_cluster"

search_parameter = {
  0: 1.0,
  1: 0.9,
  2: 0.8,
  3: 0.7,
  4: 0.6,
  5: 0.5,
  6: 0.4,
  7: 0.3,
  8: 0.2,
  9: 0.1,
}

text_store = chroma.VectorStore(os.getenv(text_model_type.upper()), text_model_type, text_collection_name)
image_store = chroma.VectorStore(os.getenv(image_model_type.upper()), image_model_type, image_collection_name)
text_cluster_store = chroma.VectorStore(None, None, text_cluster_collection_name)
image_cluster_store = chroma.VectorStore(None, None, image_cluster_collection_name)

@app.route('/', methods=['GET'])
def index():
  return "Chroma API"

@app.route('/api/search', methods=['POST'])
def search():
  data = request.json
  if not data:
    return jsonify({'error': 'No query provided'}), 400
  
  query = data.get('query')
  cnt = data.get('cnt')
  dataurls = data.get('dataurls')
  if isinstance(dataurls, str):
    dataurls = [dataurls]
  images = [utils.parse_dataurl(dataurl) for dataurl in dataurls] if dataurls else []
  
  if query:
    text_results = text_store.search(query, 
                                n=int(cnt) if cnt else 10)
    image_results = image_store.search(query,
                                n=int(cnt) if cnt else 10)
  else:
    text_results = {'ids': [], 'metadatas': [], 'documents': []}
    image_results = image_store.search(images, 
                                  n=int(cnt) if cnt else 10,
                                  query_type="image")
  
  result = {}
  if text_results['ids']:
    for i, v in enumerate(zip(
      text_results['ids'][0], 
      text_results['metadatas'][0], 
      text_results['documents'][0]
    )):
      id, metadata, document = v
      parent = metadata.get('parent')
      if not parent:
        continue
      if not result.get(parent):
        result[parent] = {
          "sum": 0.0,
          "cnt": 0,
        }
      weight = 0.5**(result[parent]["cnt"]+1)
      result[parent]["sum"] += weight * search_parameter[i]
      result[parent]["cnt"] += 1
  
  if image_results['ids']:
    for i, v in enumerate(zip(
      image_results['ids'][0], 
      image_results['metadatas'][0], 
      image_results['documents'][0]
    )):
      id, metadata, document = v
      if metadata and \
        not (parent := metadata.get('parent')) and \
        not (parent := metadata.get('resort_id')):
        continue
        
      if not result.get(parent):
        result[parent] = {
          "sum": 0.0,
          "cnt": 0,
        }
      
      weight = 0.5**(result[parent]["cnt"]+1)
      result[parent]["sum"] += weight * search_parameter[i]
      result[parent]["cnt"] += 1
  
  result = sorted([{"id": key, "score": value["sum"]} for key, value in result.items()], key=lambda x: x["score"], reverse=True)
  return jsonify({"result": result}), 200
      

@app.route('/api/chroma', methods=['POST'])
def store():
  data = request.json
  objs = data.get('objs')
  obj_type = data.get('type')
  metadata = data.get('metadata')
  
  if not objs or not obj_type:
    return jsonify({'error': 'No data provided'}), 400
  
  metadata = metadata if metadata else []
  match obj_type:
    case "text":
      embeds, docs = text_store.store(texts=objs, metas=metadata)
      text_cluster_store.add_cluster_embed(objs, embeds, metadata, docs)
    case "image":
      objs = [utils.parse_dataurl(obj) for obj in objs]
      embeds, docs = image_store.store(images=objs, metas=metadata)
      image_cluster_store.add_cluster_embed(objs, embeds, metadata, docs)
    case _:
      return jsonify({'error': 'Invalid object type'}), 400
  
  return jsonify({'message': 'Data stored successfully'}), 200
    
@app.route('/api/chroma', methods=['delete'])
def delete():
  data = request.json
  ids = data.get('ids')
  obj_type = data.get('type')
  
  if not ids or not obj_type:
    return jsonify({'error': 'No data provided'}), 400
  
  match obj_type:
    case "text":
      text_store.delete(ids)
    case "image":
      image_store.delete(ids)
    case _:
      return jsonify({'error': 'Invalid object type'}), 400
  
  return jsonify({'message': 'Data deleted successfully'}), 200


if __name__ == '__main__':
  app.run(host='0.0.0.0', port=50000)