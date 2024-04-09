#!/bin/bash
docker compose down && docker volume rm tech-talk-rag_chroma_data && docker compose up -d && docker compose logs -f ollama-tech-talk
