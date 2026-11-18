const sidebars = {
  sidebar: [
    "index",
    "getting-started",
    {
      type: "category",
      label: "Reference",
      collapsed: false,
      items: [
        "zlambda-runner",
        "context",
        "event-and-response-types",
        "events-api-gateway",
        "events-messaging",
        "events-cognito",
        "events-other",
        "responses"
      ]
    },
    "http-api-on-api-gateway",
    {
      type: "category",
      label: "How-to Guides",
      collapsed: true,
      items: [
        "testing-locally",
        "deployment",
        "consume-an-sqs-batch",
        "decode-only-the-fields-you-need"
      ]
    }
  ]
};

module.exports = sidebars;
