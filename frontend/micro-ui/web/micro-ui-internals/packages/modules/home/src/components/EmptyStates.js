import { EmptyStateIcon } from "@egovernments/digit-ui-module-dristi/src/icons/svgIndex";
import { Button } from "@egovernments/digit-ui-react-components";
// import { Button } from "@egovernments/digit-ui-components";

import React from "react";

function EmptyStates() {
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

        backgroundColor: "#fffaf6",
        padding: "20px",
        borderRadius: "8px",
        // maxWidth: "500px",
      }}
    >
      <div>
        <EmptyStateIcon />
      </div>
      <p style={{ color: "#5f5f5f", fontSize: "1.3rem", fontWeight: "bold", maxWidth: "450px", margin: "10px 0 5px 0" }}>
        An overview of this case will appear here!
      </p>
      <p style={{ color: "#6e6e6e", fontSize: "0.9rem", textAlign: "center", maxWidth: "450px", margin: "5px 0", lineHeight: "1.7" }}>
        A summary of this case’s proceedings, hearings, orders and other activities will be visible here. Take your first action on the case.
      </p>
      <div style={{ display: "flex", marginTop: "15px" }}>
        <Button variation={"secondary"} label={"Schedule Hearing"} style={{ marginRight: "10px", width: "200px" }} />
        <Button variation={"secondary"} label={"Generate Order"} style={{ marginLeft: "10px", width: "200px" }} />
      </div>
    </div>
  );
}

export default EmptyStates;
