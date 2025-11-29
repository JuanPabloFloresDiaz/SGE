import json
import os
from typing import List, Optional
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import httpx

app = FastAPI()

# --- Data Models ---

class Message(BaseModel):
    role: str
    content: str

class ChatRequest(BaseModel):
    messages: List[Message]
    model: str = "gemma2" # Default model, can be overridden

# --- Helper Functions ---

def format_math_response(text: str) -> str:
    """
    Helper function to format the LLM response with LaTeX syntax.
    This function ensures that mathematical expressions are correctly formatted
    for rendering (e.g., in a frontend that supports LaTeX).
    
    For now, it acts as a pass-through or basic cleaner.
    """
    # Placeholder for more complex regex replacements if needed.
    # For example, ensuring $$ is used for block math if the model uses \[ \] mixedly.
    return text

# --- Endpoints ---

@app.get("/")
def read_root():
    return {"message": "OllamaService (FastAPI) is running!"}

@app.post("/ask")
async def ask_ollama(request: ChatRequest):
    """
    Receives a list of messages, sends them to Ollama, and returns the full response.
    """
    ollama_host = os.getenv("OLLAMA_HOST", "ollama")
    ollama_port = os.getenv("OLLAMA_PORT", "11434")
    ollama_url = f"http://{ollama_host}:{ollama_port}/api/chat"
    
    payload = {
        "model": request.model,
        "messages": [msg.dict() for msg in request.messages],
        "stream": True 
    }

    full_response = ""
    try:
        async with httpx.AsyncClient() as client:
            # Timeout set to 60s, adjust as needed for long generations
            async with client.stream("POST", ollama_url, json=payload, timeout=120.0) as response:
                if response.status_code != 200:
                    error_detail = await response.aread()
                    raise HTTPException(status_code=response.status_code, detail=f"Ollama Error: {error_detail.decode()}")
                
                async for line in response.aiter_lines():
                    if line:
                        try:
                            data = json.loads(line)
                            if "message" in data and "content" in data["message"]:
                                full_response += data["message"]["content"]
                            if data.get("done", False):
                                break
                        except json.JSONDecodeError:
                            continue
                            
    except httpx.RequestError as exc:
        raise HTTPException(status_code=500, detail=f"Ollama connection error: {exc}")

    formatted_response = format_math_response(full_response)
    return {"response": formatted_response}