import React, { useMemo, useState } from "react";
import { AdvocateIcon, FileUploadIcon, LitigentIcon } from "../icons/svgIndex";
import EsignAdharModal from "./EsignAdharModal";
import UploadSignatureModal from "./UploadSignatureModal";
import Button from "./Button";
import { DRISTIService } from "../services";
function SignatureCard({ input, data, t, index, onSelect, formData, configKey }) {
  const [openUploadSignatureModal, setOpenUploadSignatureModal] = useState(false);
  const [openAadharModal, setOpenAadharModal] = useState(false);
  const name = `${data?.[input?.config?.title]} ${index}`;
  const uploadModalConfig = useMemo(() => {
    return {
      key: configKey,
      populators: {
        inputs: [
          {
            name: name,
            documentHeader: "Signature",
            type: "DragDropComponent",
            uploadGuidelines: "Ensure the image is not blurry and under 5MB.",
            maxFileSize: 5,
            maxFileErrorMessage: "CS_FILE_LIMIT_5_MB",
            fileTypes: ["JPG", "PNG", "JPEG"],
            isMultipleUpload: false,
          },
        ],
        validation: {},
      },
    };
  }, [configKey, name]);

  const Icon = ({ icon }) => {
    switch (icon) {
      case "LitigentIcon":
        return <LitigentIcon />;
      case "AdvocateIcon":
        return <AdvocateIcon />;
      default:
        return <LitigentIcon />;
    }
  };

  const handleAadharClick = async () => {
    try {
      const eSignResponse = await DRISTIService.eSignService({
        ESignParameter: {
          uidToken: "3456565",
          consent: "6564",
          authType: "6546",
          fileStoreId: "2aefb901-edc6-4a45-95f8-3ea383a513f5",
          tenantId: "kl",
        },
      });
      if (eSignResponse) {
        // debugger;
        // Create and submit the form programmatically
        localStorage.setItem("esignProcess", true);

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
      }
      console.log(eSignResponse);
    } catch (error) {
      console.error("API call failed:", error);
    }
  };

  const currentValue = (formData && formData[configKey] && formData[configKey][name]) || [];
  const isSigned = currentValue.length > 0;
  return (
    <div className="signature-body">
      <div className="icon-and-title">
        {input?.icon && <Icon icon={input?.icon} />}
        <h3 className="signature-title">{data?.[input?.config?.title]}</h3>
      </div>
      {isSigned && (
        <div style={{ width: "inherit", borderRadius: "30px", background: "#E4F2E4", color: "#00703C", padding: "10px", width: "fit-content" }}>
          Signed
        </div>
      )}
      {!isSigned && (
        <div className="signed-button-group">
          <Button
            icon={<FileUploadIcon />}
            label={t("CS_UPLOAD_ESIGNATURE")}
            onButtonClick={() => {
              setOpenUploadSignatureModal(true);
            }}
            className={"upload-signature"}
            labelClassName={"upload-signature-label"}
          ></Button>
          <Button
            label={t("CS_ESIGN_AADHAR")}
            onButtonClick={() => {
              handleAadharClick();
            }}
            className={"aadhar-sign-in"}
            labelClassName={"aadhar-sign-in"}
          ></Button>
        </div>
      )}
      {openUploadSignatureModal && (
        <UploadSignatureModal
          t={t}
          key={name}
          name={name}
          setOpenUploadSignatureModal={setOpenUploadSignatureModal}
          onSelect={onSelect}
          config={uploadModalConfig}
          formData={formData}
        />
      )}
      {openAadharModal && (
        <EsignAdharModal
          t={t}
          setOpenAadharModal={setOpenAadharModal}
          key={name}
          name={name}
          onSelect={onSelect}
          config={uploadModalConfig}
          formData={formData}
        />
      )}
    </div>
  );
}

export default SignatureCard;
