import React, { useEffect, useMemo, useState } from "react";
import SignatureCard from "./SignatureCard";
import { DRISTIService } from "../services";
import { useLocation } from "react-router-dom/cjs/react-router-dom.min";

function SelectSignature({ t, config, onSelect, formData = {}, errors }) {
  const inputs = useMemo(
    () =>
      config?.populators?.inputs || [
        {
          key: "complainantDetails",
          label: "CS_COMPLAINT_DETAILS",
          icon: "LitigentIcon",
          config: [{ type: "title", value: "name" }],
          data: [{ name: "Sheetal Arora" }, { name: "Mehul Das" }],
        },
      ],
    [config?.populators?.inputs]
  );

  function setValue(value, input) {
    if (Array.isArray(input)) {
      onSelect(config.key, {
        ...formData[config.key],
        ...input.reduce((res, curr) => {
          res[curr] = value[curr];
          return res;
        }, {}),
      });
    } else onSelect(config.key, { ...formData[config.key], [input]: value });
  }
  const location = useLocation();
  console.log(location);
  // const isSignSuccess = location?.state?.state?.isSignSuccess ?? false;
  // console.log(isSignSuccess);
  // useEffect(() => {
  //   if (isSignSuccess) {
  //     console.log(isSignSuccess, "fgdf");
  //   }
  // }, [isSignSuccess]);
  const handleAadharClick = async (index, data, input) => {
    try {
      const eSignResponse = await DRISTIService.eSignService({
        ESignParameter: {
          uidToken: "3456565",
          consent: "6564",
          authType: "6546",
          fileStoreId: "2aefb901-edc6-4a45-95f8-3ea383a513f5",
          tenantId: "kl",
          pageModule: "ci",
        },
      });
      if (eSignResponse) {
        // debugger;
        // Create and submit the form programmatically
        const eSignData = {
          path: window.location.pathname,
          param: window.location.search,
          isEsign: true,
          data: data,
        };
        localStorage.setItem("eSignWindowObject", JSON.stringify(eSignData));
        localStorage.setItem("esignProcess", true);
        localStorage.setItem("eSignWindowObject", window.location.href);

        const form = document.createElement("form");
        form.method = "POST";
        form.action = "https://es-staging.cdac.in/esignlevel1/2.1/form/signdoc";
        const eSignRequestInput = document.createElement("input");
        eSignRequestInput.type = "hidden";
        eSignRequestInput.name = "eSignRequest";
        eSignRequestInput.value = eSignResponse?.ESignForm?.eSignRequest;
        const aspTxnIDInput = document.createElement("input");
        aspTxnIDInput.type = "hidden";
        aspTxnIDInput.name = "aspTxnID";
        aspTxnIDInput.value = eSignResponse?.ESignForm?.aspTxnID;
        const contentTypeInput = document.createElement("input");
        contentTypeInput.type = "hidden";
        contentTypeInput.name = "Content-Type";
        contentTypeInput.value = "application/xml";
        form.appendChild(eSignRequestInput);
        form.appendChild(aspTxnIDInput);
        form.appendChild(contentTypeInput);
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);
        const name = `${data?.[input?.config?.title]} ${index}`;
        setValue(["aadharsignature"], name);
      }
    } catch (error) {
      console.error("API call failed:", error);
    }
  };
  return (
    <div className="select-signature-main">
      {inputs.map((input, inputIndex) => (
        <React.Fragment>
          <div className="select-signature-header">
            <h1 className="signature-label">{config?.label}</h1>
          </div>
          <div className="select-signature-list">
            {input.data.map((item, itemIndex) => (
              <SignatureCard
                key={inputIndex + itemIndex}
                index={itemIndex}
                data={item}
                input={input}
                t={t}
                formData={formData}
                onSelect={onSelect}
                configKey={config.key}
                handleAadharClick={handleAadharClick}
              />
            ))}
          </div>
        </React.Fragment>
      ))}
    </div>
  );
}

export default SelectSignature;
