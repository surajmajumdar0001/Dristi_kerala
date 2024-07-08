import React, { useEffect } from "react";
import { useHistory, useLocation } from "react-router-dom/cjs/react-router-dom.min";

function PaymentGatewayPage({ t }) {
  const history = useHistory();
  const location = useLocation();

  const html = location?.state?.state;
  // const html = html;
  const updatedHtmlString = html.replace(
    "ChallanGeneration.php",
    "https://www.stagingetreasury.kerala.gov.in/api/eTreasury/service/ChallanGeneration.php"
  );
  console.log(updatedHtmlString);
  return (
    <React.Fragment>
      <iframe srcdoc={updatedHtmlString} target="_parent" style={{ width: "100%", height: "100vh" }}></iframe>
    </React.Fragment>
  );
}

export default PaymentGatewayPage;
