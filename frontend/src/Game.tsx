import React, { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";

const Arena = () => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const stompClientRef = useRef<Client | null>(null);
  const [currentUser, setCurrentUser] = useState<any>({});
  const [users, setUsers] = useState(new Map());
  const [params, setParams] = useState({ token: "", spaceId: "" });

  // Initialize STOMP connection and handle URL params
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token") || "";
    const spaceId = urlParams.get("spaceId") || "";
    setParams({ token, spaceId });

    const stompClient = new Client({
      brokerURL: "ws://localhost:5456/ws", // Replace with your STOMP URL
      onConnect: () => {
        // Subscribe to relevant topics
        stompClient.subscribe(`/topic/space/${spaceId}`, (message) => {
          const parsedMessage = JSON.parse(message.body);
          handleStompMessage(parsedMessage);
        });

        // Send "join" message
        stompClient.publish({
          destination: "/app/space",
          body: JSON.stringify({
            type: "JOIN",
            payload: {
              userId: "need admin id as well",
              spaceId,
              token: "Bearer " + token,
            },
          }),
        });
      },
      onStompError: (frame) => {
        console.error("Broker reported error:", frame.headers["message"]);
        console.error("Additional details:", frame.body);
      },
    });

    stompClient.activate();
    stompClientRef.current = stompClient;

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, []);

  const handleStompMessage = (message: any) => {
    switch (message.type) {
      case "SPACE_JOINED":
        const x: any = message.payload.x;
        const y: any = message.payload.y;
        const userId = message.payload.userId;
        localStorage.setItem("wsuserId", userId);

        setCurrentUser({
          x: message.payload.x,
          y: message.payload.y,
          userId: message.payload.userId,
        });
        const newUsers = new Map(users);
        newUsers.set(userId, { x, y });
        setUsers(newUsers);
        break;

      case "MOVE":
        setUsers((prev) => {
          const newUsers = new Map(prev);
          const user = newUsers.get(message.payload.userId);
          if (user) {
            user.x = message.payload.x;
            user.y = message.payload.y;
            newUsers.set(message.payload.userId, user);
          }
          return newUsers;
        });
        break;

      case "PING":
        const userid = localStorage.getItem("wsuserId");
        const user = users.get(userId);
        const x = user.x;
        const y = user.y;
        stompClientRef.current.publish({
          destination: "/app/space/ping",
          body: JSON.stringify({
            type: "PONG",
            //////how to get user name will need make changes to backend for that AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
          }),
        });
        break;
    }
  };

  const handleMove = (newX: number, newY: number) => {
    if (!currentUser || !stompClientRef.current) return;

    stompClientRef.current.publish({
      destination: "/app/move",
      body: JSON.stringify({
        x: newX,
        y: newY,
        userId: currentUser.userId,
      }),
    });
  };

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Draw grid
    ctx.strokeStyle = "#eee";
    for (let i = 0; i < canvas.width; i += 50) {
      ctx.beginPath();
      ctx.moveTo(i, 0);
      ctx.lineTo(i, canvas.height);
      ctx.stroke();
    }
    for (let i = 0; i < canvas.height; i += 50) {
      ctx.beginPath();
      ctx.moveTo(0, i);
      ctx.lineTo(canvas.width, i);
      ctx.stroke();
    }

    // Draw current user
    if (currentUser && currentUser.x !== undefined) {
      ctx.beginPath();
      ctx.fillStyle = "#FF6B6B";
      ctx.arc(currentUser.x * 50, currentUser.y * 50, 20, 0, Math.PI * 2);
      ctx.fill();
      ctx.fillStyle = "#000";
      ctx.font = "14px Arial";
      ctx.textAlign = "center";
      ctx.fillText("You", currentUser.x * 50, currentUser.y * 50 + 40);
    }

    // Draw other users
    users.forEach((user) => {
      if (!user.x) return;
      ctx.beginPath();
      ctx.fillStyle = "#4ECDC4";
      ctx.arc(user.x * 50, user.y * 50, 20, 0, Math.PI * 2);
      ctx.fill();
      ctx.fillStyle = "#000";
      ctx.font = "14px Arial";
      ctx.textAlign = "center";
      ctx.fillText(`User ${user.userId}`, user.x * 50, user.y * 50 + 40);
    });
  }, [currentUser, users]);

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (!currentUser) return;

    const { x, y } = currentUser;
    switch (e.key) {
      case "ArrowUp":
        handleMove(x, y - 1);
        break;
      case "ArrowDown":
        handleMove(x, y + 1);
        break;
      case "ArrowLeft":
        handleMove(x - 1, y);
        break;
      case "ArrowRight":
        handleMove(x + 1, y);
        break;
    }
  };

  return (
    <div className="p-4" onKeyDown={handleKeyDown} tabIndex={0}>
      <h1 className="text-2xl font-bold mb-4">Arena</h1>
      <div className="mb-4">
        <p className="text-sm text-gray-600">Token: {params.token}</p>
        <p className="text-sm text-gray-600">Space ID: {params.spaceId}</p>
        <p className="text-sm text-gray-600">
          Connected Users: {users.size + (currentUser ? 1 : 0)}
        </p>
      </div>
      <div className="border rounded-lg overflow-hidden">
        <canvas
          ref={canvasRef}
          width={2000}
          height={2000}
          className="bg-white"
        />
      </div>
      <p className="mt-2 text-sm text-gray-500">
        Use arrow keys to move your avatar
      </p>
    </div>
  );
};

export default Arena;
