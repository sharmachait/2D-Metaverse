import React, { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { jwtDecode } from "jwt-decode";

interface CustomJwtPayload {
  email: string;
  id: string;
}
interface User {
  x?: number;
  y?: number;
  userId?: string;
  username?: string;
}
const Arena = () => {
  function getEmailFromToken(token: string) {
    try {
      const decoded = jwtDecode<CustomJwtPayload>(token);
      return decoded.email;
    } catch (error) {
      if (error instanceof Error) {
        throw new Error("Failed to decode JWT token: " + error.message);
      }
      throw new Error("Failed to decode JWT token: Unknown error");
    }
  }
  function getIdFromToken(token: string) {
    try {
      const decoded = jwtDecode<CustomJwtPayload>(token);
      return decoded.id;
    } catch (error) {
      if (error instanceof Error) {
        throw new Error("Failed to decode JWT token: " + error.message);
      }
      throw new Error("Failed to decode JWT token: Unknown error");
    }
  }
  // // eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3Mzg0MDQxOTMsImV4cCI6MTczODQ5MDU5MywiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJjaGFpdGFueWFzaGFybWEiLCJpZCI6IjY1NzNlYTU4LWNjZWItNGNjYS1iMzg0LTgzOTIwMjM0ZGU5MCJ9.j79iu18OYBzJQH5GKrrisTObGquH9Va6Bz3VYjlMs2I
  // // 2113bfab-bce5-4af3-8aff-159c3392b1af
  // // eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3Mzg0MDQyMTQsImV4cCI6MTczODQ5MDYxNCwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJjaGFpdGFueWFkc2hhcm1hIiwiaWQiOiJiYmYyNWVmZi1kNGMwLTQyODctODhhMC0yZjEyNmVmNjZmN2QifQ.J5149IrcrTSISzKg5-iDhxczjTdOWIpteGxSJOWQ73c
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const stompClientRef = useRef<Client | null>(null);
  const userStompClientRef = useRef<Client | null>(null);
  const [currentUser, setCurrentUser] = useState<User>({});
  const [users, setUsers] = useState(new Map());
  const [params, setParams] = useState({ token: "", spaceId: "" });

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token") || "";
    const spaceId = urlParams.get("spaceId") || "";
    setParams({ token, spaceId });
    console.log({ token, spaceId });
    console.log(getEmailFromToken(token));
    console.log(getIdFromToken(token));
    console.log(
      "------------------------------------------------------------------"
    );

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:5457/ws"),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = (frame) => {
      client.subscribe(`/topic/space/${params.spaceId}`, (message) => {
        const parsedMessage = JSON.parse(message.body);
        handleStompMessage(parsedMessage);
      });
    };
    client.activate();
    stompClientRef.current = client;
    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, []);

  const handleStompMessage = (message) => {
    switch (message.type) {
      case "SPACE_JOINED": {
        const x = message.payload.x;
        const y = message.payload.y;
        const userId = message.payload.userId;
        const username = message.payload.username;
        localStorage.setItem("wsuserId", userId);

        setCurrentUser({
          x: message.payload.x,
          y: message.payload.y,
          userId: message.payload.userId,
        });
        const newUsers = new Map(users);
        newUsers.set(userId, { x, y, username });
        setUsers(newUsers);

        if (username === getEmailFromToken(params.token)) {
          const client = new Client({
            webSocketFactory: () => new SockJS("http://localhost:5457/ws"),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            // connectHeaders: {
            //   "Access-Control-Allow-Origin": "*",
            // },
          });

          client.onConnect = (frame) => {
            const username = getEmailFromToken(params.token);
            client.subscribe(`/user/${username}/queue/messages`, (message) => {
              const parsedMessage = JSON.parse(message.body);
              handleStompMessage(parsedMessage);
            });
          };
          client.activate();
          userStompClientRef.current = client;
        }

        break;
      }
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

      case "PING": {
        const userid = localStorage.getItem("wsuserId");
        const user = users.get(userid);
        const x = user.x;
        const y = user.y;
        stompClientRef.current.publish({
          destination: "/app/space/ping",
          body: JSON.stringify({
            type: "PONG",
            payload: {
              userFor: message.payload.userFor,
              userFrom: user.username,
              userFromId: userid,
              token: "Bearer " + params.token,
              spaceId: params.spaceId,
              fromX: x,
              fromY: y,
            },
          }),
        });
        break;
      }

      case "PONG":
        setUsers((prev) => {
          const newUsers = new Map(prev);
          const userFromId = message.payload.userFromId;
          const user = newUsers.get(userFromId) || {};
          const fromX = message.payload.fromX;
          const fromY = message.payload.fromY;
          const userFrom = message.payload.userFrom;
          newUsers.set(userFromId, {
            ...user,
            x: fromX,
            y: fromY,
            username: userFrom,
          });
          return newUsers;
        });
        break;
    }
  };

  const handleMove = (newX: number, newY: number) => {
    if (!currentUser || !stompClientRef.current) return;

    stompClientRef.current.publish({
      destination: "/app/space/move",
      body: JSON.stringify({
        type: "MOVE",
        payload: {
          x: newX,
          y: newY,
          token: "Bearer " + params.token,
          spaceId: params.spaceId,
          userId: currentUser.userId,
        },
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
