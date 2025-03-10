import React, { useEffect, useMemo, useState } from "react";
import { AdvocateIcon, FileUploadIcon, LitigentIcon } from "../icons/svgIndex";
import EsignAdharModal from "./EsignAdharModal";
import UploadSignatureModal from "./UploadSignatureModal";
import Button from "./Button";
import { useLocation } from "react-router-dom/cjs/react-router-dom.min";
import { isEqual } from "lodash";

function SignatureCard({ input, data, t, index, onSelect, formData, configKey, handleAadharClick }) {
  const [openUploadSignatureModal, setOpenUploadSignatureModal] = useState(false);
  const [openAadharModal, setOpenAadharModal] = useState(false);
  const [formDataCopy, setFormData] = useState({});
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
  function setValue(value, input) {
    if (Array.isArray(input)) {
      onSelect(uploadModalConfig.key, {
        ...formData[uploadModalConfig.key],
        ...input.reduce((res, curr) => {
          res[curr] = value[curr];
          return res;
        }, {}),
      });
    } else onSelect(uploadModalConfig.key, { ...formData[uploadModalConfig.key], [input]: value });
  }
  const isSignSuccess = useMemo(() => localStorage.getItem("isSignSuccess"), []);
  const storedESignObj = useMemo(() => localStorage.getItem("signStatus"), []);
  const parsedESignObj = JSON.parse(storedESignObj);

  useEffect(() => {
    if (isSignSuccess) {
      const matchedSignStatus = parsedESignObj.find((obj) => obj.name === name && obj.isSigned == true);
      if (isSignSuccess === "success" && matchedSignStatus) {
        if (!isEqual(formData, formDataCopy)) {
          setValue(["aadharsignature"], name);
          setFormData(formData);
        }
      }
      localStorage.removeItem("name");
      localStorage.removeItem("isSignSuccess");
    }
  }, [isSignSuccess, formData]);
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

  const currentValue = (formData && formData[configKey] && formData[configKey][name]) || [];
  const isSigned = currentValue.length > 0;
  return (
    <div className="signature-body">
      <div className="icon-and-title">
        {input?.icon && <Icon icon={input?.icon} />}
        <h3 className="signature-title">{data?.[input?.config?.title]}</h3>
      </div>
      {isSigned && <span className="signed">{t("SIGNED")}</span>}
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
              handleAadharClick(data, name);
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
