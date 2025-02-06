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
  // eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3Mzg4NjczMDgsImV4cCI6MTczODk1MzcwOCwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJjaGFpdGFuc3lhZHNoYXJtYSIsImlkIjoiNDZiMTk5NGQtOWJmZS00MzZhLTkxYTMtOThiN2E2NWM5NzdjIn0.sipupaCTdccnfi0rx8-QSYXqkdmJCtGwWYZ0EHXSLV4
  // f1877458-29d2-449d-a693-51edd3d38e19
  // eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3Mzg4NjcyOTksImV4cCI6MTczODk1MzY5OSwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJjaGFpdGFuc3lhc2hhcm1hIiwiaWQiOiI5YjhmMzA3NS02NTE4LTRjYWUtODg5OS00MWRhNDgyNTAzZjUifQ.B-8aFiVfxZPg4oHNiDGJqMQtBYUCAadSIHB-e0eoB70
  // http://localhost:5173/?token=eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3Mzg4NjczMDgsImV4cCI6MTczODk1MzcwOCwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJjaGFpdGFuc3lhZHNoYXJtYSIsImlkIjoiNDZiMTk5NGQtOWJmZS00MzZhLTkxYTMtOThiN2E2NWM5NzdjIn0.sipupaCTdccnfi0rx8-QSYXqkdmJCtGwWYZ0EHXSLV4&spaceId=f1877458-29d2-449d-a693-51edd3d38e19
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const stompClientRef = useRef<Client | null>(null);
  const userStompClientRef = useRef<Client | null>(null);

  const currentUserLocalStorage = () => {
    return {
      x: localStorage.getItem("x"),
      y: localStorage.getItem("y"),
      userId: localStorage.getItem("userId"),
      username: localStorage.getItem("username"),
      token: localStorage.getItem("token"),
    };
  };
  const setCurrentUserLocalStorage = (prev: any) => {
    if (prev.x) localStorage.setItem("x", prev.x.toString());
    if (prev.y) localStorage.setItem("y", prev.y.toString());
    if (prev.userId) localStorage.setItem("userId", prev.userId);
    if (prev.username) localStorage.setItem("username", prev.username);
    if (prev.token) localStorage.setItem("token", prev.token);
  };

  const [users, setUsers] = useState(new Map());
  const [params, setParams] = useState({ token: "", spaceId: "" });

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token") || "";
    const spaceId = urlParams.get("spaceId") || "";
    setCurrentUserLocalStorage({ token });
    setParams({ token, spaceId });

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:5457/ws"),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = (frame) => {
      console.log("subscribing");
      console.log({ params });
      client.subscribe(`/topic/space/${spaceId}`, (message) => {
        const parsedMessage = JSON.parse(message.body);
        console.log(
          "------------------------------------------------------------------"
        );
        console.log({ parsedMessage });
        console.log(
          "------------------------------------------------------------------"
        );
        console.log({ params });
        handleStompMessage(parsedMessage, token, spaceId);
      });
      console.log("joining");

      client.publish({
        destination: "/app/space",
        body: JSON.stringify({
          type: "JOIN",
          payload: {
            senderId: getIdFromToken(token),
            userId: getIdFromToken(token),
            spaceId: spaceId,
            token: "Bearer " + token,
          },
        }),
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

  const handleStompMessage = (message, token, spaceId) => {
    switch (message.type) {
      case "SPACE_JOINED":
        joined();
        break;
      case "MOVE":
        move();
        break;
      case "PING":
        ping();
        break;
      case "PONG":
        pong();
        break;
    }

    function pong() {
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
    }

    function ping() {
      console.log({ email: getEmailFromToken(token) });

      if (getEmailFromToken(token) !== currentUserLocalStorage().username) {
        const userid = currentUserLocalStorage().userId;
        const x = currentUserLocalStorage().x;
        const y = currentUserLocalStorage().y;
        stompClientRef.current.publish({
          destination: "/app/space/ping",
          body: JSON.stringify({
            type: "PONG",
            payload: {
              userFor: message.payload.userFor,
              userFrom: currentUserLocalStorage().username,
              userFromId: userid,
              token: "Bearer " + token,
              spaceId: spaceId,
              fromX: x,
              fromY: y,
            },
          }),
        });
      }
    }

    function move() {
      console.log(
        "***************************************************************"
      );

      console.log({ "current user id": currentUserLocalStorage().userId });
      console.log({ "user id from payload": message.payload.userId });
      if (currentUserLocalStorage().userId === message.payload.userId) {
        setCurrentUserLocalStorage({
          x: message.payload.x,
          y: message.payload.y,
          userId: message.payload.userId,
        });
      }
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
    }

    function joined() {
      const x = message.payload.x;
      const y = message.payload.y;
      const userId = message.payload.userId;
      const username = message.payload.username;

      if (username === getEmailFromToken(token)) {
        setCurrentUserLocalStorage({ x: 0, y: 0, userId, username });

        const client = new Client({
          webSocketFactory: () => new SockJS("http://localhost:5457/ws"),
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
        });

        client.onConnect = (frame) => {
          const username = getEmailFromToken(params.token);

          client.subscribe(`/user/${username}/queue/messages`, (message) => {
            const parsedMessage = JSON.parse(message.body);

            console.log(
              "------------------------------------------------------------------"
            );
            console.log({ parsedMessage });

            handleStompMessage(parsedMessage, token, spaceId);
          });
        };
        client.activate();
        userStompClientRef.current = client;
      }

      const newUsers = new Map(users);
      newUsers.set(userId, { x, y, username });
      setUsers(newUsers);

      console.log({
        "currentUser in LocalStorage": currentUserLocalStorage(),
      });
    }
  };

  const handleMove = (newX: number, newY: number) => {
    if (!currentUserLocalStorage().userId || !stompClientRef.current) return;

    stompClientRef.current.publish({
      destination: "/app/space/move",
      body: JSON.stringify({
        type: "MOVE",
        payload: {
          x: newX,
          y: newY,
          token: "Bearer " + params.token,
          spaceId: params.spaceId,
          userId: currentUserLocalStorage().userId,
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
    if (
      currentUserLocalStorage().userId &&
      currentUserLocalStorage().x !== undefined
    ) {
      ctx.beginPath();
      ctx.fillStyle = "#FF6B6B";
      ctx.arc(
        currentUserLocalStorage().x * 50,
        currentUserLocalStorage().y * 50,
        20,
        0,
        Math.PI * 2
      );
      ctx.fill();
      ctx.fillStyle = "#000";
      ctx.font = "14px Arial";
      ctx.textAlign = "center";
      ctx.fillText(
        "You",
        currentUserLocalStorage().x * 50,
        currentUserLocalStorage().y * 50 + 40
      );
    }

    // Draw other users
    users.forEach((user) => {
      if (!user.x) return;
      if (user.userId === currentUserLocalStorage().userId) return;
      ctx.beginPath();
      ctx.fillStyle = "#4ECDC4";
      ctx.arc(user.x * 50, user.y * 50, 20, 0, Math.PI * 2);
      ctx.fill();
      ctx.fillStyle = "#000";
      ctx.font = "14px Arial";
      ctx.textAlign = "center";
      ctx.fillText(`User ${user.userId}`, user.x * 50, user.y * 50 + 40);
    });
  }, [users]);

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (!currentUserLocalStorage().userId) return;

    const x = currentUserLocalStorage().x;
    const y = currentUserLocalStorage().y;
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
        <p className="text-sm text-gray-600">Users: {users.size}</p>
        <p className="text-sm text-gray-600">
          current User: {currentUserLocalStorage().userId ? 1 : 0}
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
