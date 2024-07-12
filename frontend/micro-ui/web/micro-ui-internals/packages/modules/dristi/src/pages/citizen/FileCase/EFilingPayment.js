import { Banner, CardLabel, CloseSvg, Loader, Modal } from "@egovernments/digit-ui-react-components";
import React, { useMemo, useState } from "react";
import Button from "../../../components/Button";
import { InfoCard } from "@egovernments/digit-ui-components";
import { Link, useHistory } from "react-router-dom/cjs/react-router-dom.min";
import CustomCaseInfoDiv from "../../../components/CustomCaseInfoDiv";
import useSearchCaseService from "../../../hooks/dristi/useSearchCaseService";
import { useToast } from "../../../components/Toast/useToast";
import usePaymentCalculator from "../../../hooks/dristi/usePaymentCalculator";
import { DRISTIService } from "../../../services";

const mockSubmitModalInfo = {
  header: "CS_HEADER_FOR_E_FILING_PAYMENT",
  subHeader: "CS_SUBHEADER_TEXT_FOR_E_FILING_PAYMENT",
  caseInfo: [
    {
      key: "Case Number",
      value: "FSM-2019-04-23-898898",
    },
  ],
  isArrow: false,
  showTable: true,
};

const CloseBtn = (props) => {
  return (
    <div onClick={props?.onClick} style={{ height: "100%", display: "flex", alignItems: "center", paddingRight: "20px", cursor: "pointer" }}>
      <CloseSvg />
    </div>
  );
};

const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};

function EFilingPayment({ t, setShowModal, header, subHeader, submitModalInfo = mockSubmitModalInfo, amount = 2000, path }) {
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const history = useHistory();
  const onCancel = () => {
    setShowPaymentModal(false);
  };
  const tenantId = window?.Digit.ULBService.getCurrentTenantId();
  const { caseId } = window?.Digit.Hooks.useQueryParams();
  const toast = useToast();
  const [paymentLoader, setPaymentLoader] = useState(false);

  const { data: caseData, isLoading } = useSearchCaseService(
    {
      criteria: [
        {
          caseId: caseId,
        },
      ],
      tenantId,
    },
    {},
    "dristi",
    caseId,
    caseId
  );

  const caseDetails = useMemo(
    () => ({
      ...caseData?.criteria?.[0]?.responseList?.[0],
    }),
    [caseData]
  );
  const chequeDetails = useMemo(
    () => ({
      ...caseDetails?.caseDetails?.chequeDetails?.formdata?.[0],
    }),
    [caseDetails]
  );
  const { data: calculationResponse, isLoading: isPaymentLoading } = usePaymentCalculator(
    {
      EFillingCalculationCriteria: [
        {
          checkAmount: chequeDetails?.data?.chequeAmount.toString(),
          numberOfApplication: 1,
          tenantId: tenantId,
          caseId: caseId,
        },
      ],
    },
    {},
    "dristi",
    Boolean(chequeDetails?.data?.chequeAmount)
  );

  const totalAmount = useMemo(() => {
    const totalAmount = calculationResponse?.Calculation?.[0]?.totalAmount || 0;
    return totalAmount;
  }, [calculationResponse?.Calculation]);
  const paymentCalculation = useMemo(() => {
    const breakdown = calculationResponse?.Calculation?.[0]?.breakDown || [];
    const updatedCalculation = breakdown.map((item) => ({
      key: item?.type,
      value: item?.amount,
      currency: "Rs",
    }));

    updatedCalculation.push({
      key: "Total amount",
      value: totalAmount,
      currency: "Rs",
      isTotalFee: true,
    });

    return updatedCalculation;
  }, [calculationResponse?.Calculation]);
  const submitInfoData = useMemo(() => {
    return {
      ...mockSubmitModalInfo,
      caseInfo: [
        {
          key: "CS_CASE_NUMBER",
          value: caseDetails?.filingNumber,
          copyData: true,
        },
      ],
      isArrow: false,
      showTable: true,
      showCopytext: true,
    };
  }, [caseDetails?.filingNumber]);

  const openPopupWindow = (htmlContent) => {
    const popup = window.open("", "Popup", "width=1000,height=1000");

    popup.document.open();
    popup.document.write(htmlContent);
    setPaymentLoader(true);
    popup.document.close();
    const checkPopupClosed = setInterval(async () => {
      if (popup.closed) {
        setPaymentLoader(false);
        const billAfterPayment = await DRISTIService.callSearchBill({}, { tenantId, consumerCode: caseDetails?.filingNumber, service: "case" });
        console.log(billAfterPayment?.ResposneInfo?.status);
        if (billAfterPayment?.Bill?.[0]?.status === "ACTIVE") {
          history.push(`${path}/e-filing-payment-response`, {
            state: {
              success: true,
              receiptData: {
                ...mockSubmitModalInfo,
                caseInfo: [
                  {
                    key: "Mode of Payment",
                    value: "Online",
                    copyData: false,
                  },
                  {
                    key: "Amount",
                    value: totalAmount,
                    copyData: false,
                  },
                  {
                    key: "Transaction ID",
                    value: caseDetails?.filingNumber,
                    copyData: true,
                  },
                ],
                isArrow: false,
                showTable: true,
                showCopytext: true,
              },
            },
          });
        } else {
          history.push(`${path}/e-filing-payment-response`, {
            state: {
              success: false,
              receiptData: {
                ...mockSubmitModalInfo,
                caseInfo: [
                  {
                    key: "Mode of Payment",
                    value: "Online",
                    copyData: false,
                  },
                  {
                    key: "Amount",
                    value: totalAmount,
                    copyData: false,
                  },
                  {
                    key: "Transaction ID",
                    value: caseDetails?.filingNumber,
                    copyData: true,
                  },
                ],
                isArrow: false,
                showTable: true,
                showCopytext: true,
              },
            },
          });
        }
        clearInterval(checkPopupClosed);
      }
    }, 1000);
    setShowPaymentModal(false);
  };
  const onSubmitCase = async () => {
    try {
      const demandResponse = await DRISTIService.createDemand({
        Demands: [
          {
            tenantId,
            consumerCode: caseDetails?.filingNumber,
            consumerType: "case",
            businessService: "case",
            taxPeriodFrom: Date.now().toString(),
            taxPeriodTo: Date.now().toString(),
            demandDetails: [
              {
                taxHeadMasterCode: "CASE_ADVANCE_CARRYFORWARD",
                taxAmount: totalAmount,
                collectionAmount: 0,
              },
            ],
          },
        ],
      });

      const bill = await DRISTIService.callFetchBill({}, { consumerCode: caseDetails?.filingNumber, tenantId, businessService: "case" });

      if (bill) {
        const gateway = await DRISTIService.callETreasury(
          {
            ChallanData: {
              ChallanDetails: {
                FROM_DATE: "26/02/2020",
                TO_DATE: "26/02/2020",
                PAYMENT_MODE: "E",
                NO_OF_HEADS: "1",
                HEADS_DET: [
                  {
                    AMOUNT: "2",
                    HEADID: "00374",
                  },
                ],
                CHALLAN_AMOUNT: "2",
                PARTY_NAME: caseDetails?.additionalDetails?.payerName,
                DEPARTMENT_ID: bill?.Bill?.[0]?.billDetails?.[0]?.id,
                TSB_RECEIPTS: "N",
              },
              billId: bill?.Bill?.[0]?.billDetails?.[0]?.billId,
              serviceNumber: caseDetails?.filingNumber,
              businessService: "case",
              totalDue: totalAmount,
              mobileNumber: "9876543210",
              paidBy: "COMMON_OWNER",
            },
          },
          {}
        );

        if (gateway) {
          const updatedHtmlString = gateway?.htmlPage?.htmlString.replace(
            "ChallanGeneration.php",
            "https://www.stagingetreasury.kerala.gov.in/api/eTreasury/service/ChallanGeneration.php"
          );
          openPopupWindow(updatedHtmlString);
        } else {
          handleError("Error calling e-Treasury.");
        }
      }
    } catch (error) {
      handleError(`Error in onSubmitCase: ${error.message}`);
    }
  };

  if (isLoading || isPaymentLoading || paymentLoader) {
    return <Loader />;
  }
  return (
    <div className=" user-registration">
      <div className="e-filing-payment">
        <Banner
          whichSvg={"tick"}
          successful={true}
          message={t(submitModalInfo?.header)}
          headerStyles={{ fontSize: "32px" }}
          style={{ minWidth: "100%" }}
        ></Banner>
        {submitInfoData?.subHeader && <CardLabel className={"e-filing-card-label"}>{t(submitInfoData?.subHeader)}</CardLabel>}
        {submitInfoData?.showTable && (
          <CustomCaseInfoDiv
            t={t}
            data={submitInfoData?.caseInfo}
            tableDataClassName={"e-filing-table-data-style"}
            tableValueClassName={"e-filing-table-value-style"}
          />
        )}
        <div className="button-field">
          <Button
            variation={"secondary"}
            className={"secondary-button-selector"}
            label={t("CS_GO_TO_HOME")}
            labelClassName={"secondary-label-selector"}
            style={{ minWidth: "30%" }}
            onButtonClick={() => {
              history.push(`/${window?.contextPath}/citizen/dristi/home`);
            }}
          />
          <Button
            variation={"secondary"}
            className={"secondary-button-selector"}
            label={t("CS_PRINT_CASE_FILE")}
            labelClassName={"secondary-label-selector"}
            style={{ minWidth: "30%" }}
            onButtonClick={() => {}}
          />
          <Button
            className={"tertiary-button-selector"}
            label={t("CS_MAKE_PAYMENT")}
            labelClassName={"tertiary-label-selector"}
            style={{ minWidth: "30%" }}
            onButtonClick={() => {
              setShowPaymentModal(true);
            }}
          />
        </div>
        {showPaymentModal && (
          <Modal
            headerBarEnd={<CloseBtn onClick={onCancel} />}
            actionSaveLabel={t("CS_PAY_ONLINE")}
            formId="modal-action"
            actionSaveOnSubmit={() => onSubmitCase()}
            headerBarMain={<Heading label={t("CS_PAY_TO_FILE_CASE")} />}
          >
            <div className="payment-due-wrapper" style={{ display: "flex", flexDirection: "column", maxHeight: "200px !important" }}>
              <div className="payment-due-text" style={{ fontSize: "18px" }}>
                {`${t("CS_DUE_PAYMENT")} `}
                <span style={{ fontWeight: 700 }}>Rs {totalAmount}/-.</span>
                {` ${t("CS_MANDATORY_STEP_TO_FILE_CASE")}`}
              </div>
              <div className="payment-calculator-wrapper" style={{ display: "flex", flexDirection: "column" }}>
                {paymentCalculation.map((item) => (
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      borderTop: item.isTotalFee && "1px solid #BBBBBD",
                      fontSize: item.isTotalFee && "16px",
                      fontWeight: item.isTotalFee && "700",
                      paddingTop: item.isTotalFee && "12px",
                    }}
                  >
                    <span>{item.key}</span>
                    <span>
                      {item.currency} {item.value}
                    </span>
                  </div>
                ))}
              </div>
              <div>
                <InfoCard
                  variant={"default"}
                  label={t("CS_COMMON_NOTE")}
                  style={{ margin: "16px 0 0 0", backgroundColor: "#ECF3FD" }}
                  additionalElements={[
                    <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                      <span>{t("CS_OFFLINE_PAYMENT_STEP_TEXT")}</span>
                      <Link style={{ fontWeight: 700, color: "#0A0A0A" }}>{t("CS_LEARN_MORE")}</Link>
                    </div>,
                  ]}
                  inline
                  textStyle={{}}
                  className={"adhaar-verification-info-card"}
                />
              </div>
            </div>
          </Modal>
        )}
      </div>
    </div>
  );
}

export default EFilingPayment;
