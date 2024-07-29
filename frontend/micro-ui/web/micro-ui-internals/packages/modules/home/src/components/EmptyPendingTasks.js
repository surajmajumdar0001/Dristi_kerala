import React from "react";

function EmptyPendingTasks() {
  return (
    <div
      style={{
        marginLeft: "auto",
        marginRight: "auto",
        width: "100%",
        height: "500px",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#f9fafb",
        padding: "20px",
        borderRadius: "8px",
      }}
    >
      <p style={{ color: "#5f5f5f", fontSize: "1.3rem", fontWeight: "bold", maxWidth: "300px", margin: "5px 0", textAlign: "center" }}>
        All pending actions for this case will appear here
      </p>
    </div>
  );
}

export default EmptyPendingTasks;
