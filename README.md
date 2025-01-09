![Virtual Space Platform Demo](./thumpnail.jpg)

# Gather.town Clone
A real-time virtual space platform built with Spring Boot, WebSocket, and WebRTC that allows users to create and navigate virtual spaces while enabling proximity-based chat and video calling.

## Features

### Space Management
- **Custom Space Creation:** Admins can build and customize virtual spaces
- **Dynamic Map Building:** Create interactive layouts with walls, meeting areas, and gathering spots
- **Multiple Spaces:** Support for multiple concurrent virtual environments

### Real-time Interaction
- **Live Movement:** Users can move their avatars through spaces in real-time
- **Proximity Detection:** System automatically detects when users are within interaction range
- **User Position Sync:** Real-time synchronization of all users' positions across the space

### Communication Features
- **Proximity Chat:** Automatically connect users for text chat when they're close to each other
- **Video Calling:** One-on-one video calls between users in close proximity
- **WebRTC Integration:** Peer-to-peer video communication for better performance

### Authentication & Security
- **User Authentication:** Secure login system with JWT tokens
- **Role-Based Access:** Different permissions for admins and regular users
- **Secure WebSocket:** Protected WebSocket connections for real-time updates

## Technology Stack

### Backend
- ✅ Spring Boot
- ✅ WebSocket (STOMP)
- ✅ Spring Security
- ✅ PostgreSQL
- ✅ JWT Authentication
- 🚧 Azure/AWS/Zego cloud (for video calling)
- 🚧 Docker
- 🚧 Kubernetes
- 🚧 Azure Pipelines for CI/CD
- 🚧 Azure Build Agents (self-hosted docker with docker-in-docker capabilities)

### Frontend
- 🚧 HTML/CSS/JavaScript/TypeScript
- 🚧 Tailwind CSS
- 🚧 React.js
- 🚧 STOMP.js for WebSocket communication

## Current Status
✅ = Implemented  
🚧 = Planned/In Progress
