import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/combo-box/src/vaadin-combo-box.js';
import 'Frontend/generated/jar-resources/comboBoxConnector.js';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/integer-field/src/vaadin-integer-field.js';
import '@vaadin/icons/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/vaadin-big-decimal-field.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/dialog/src/vaadin-dialog.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/app-layout/src/vaadin-app-layout.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/context-menu/src/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/multi-select-combo-box/src/vaadin-multi-select-combo-box.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/grid/src/vaadin-grid-column.js';
import '@vaadin/grid/src/vaadin-grid-sorter.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/gridConnector.ts';
import '@vaadin/button/src/vaadin-button.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/avatar/src/vaadin-avatar.js';
import '@vaadin/grid/src/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/notification/src/vaadin-notification.js';
import '@vaadin/login/src/vaadin-login-form.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '931d5a37a5b368349859e161eb1b206c3e0c6254cbfdd1cfe7be6bf362119cf6') {
    pending.push(import('./chunks/chunk-2c7410c21ae30f6a58304b2fe3575fb4c1686efeb4ecfdcc68691c7acd3c10a7.js'));
  }
  if (key === '5a8a8fc2bc01b113b0ab602875ab4fd325e72e9ce9677c98e65eb56c17c4eeff') {
    pending.push(import('./chunks/chunk-709d3dde7f97c0ce98b91bc51b9d436c56fa0d22835af0c0eed7b13ea50c7b80.js'));
  }
  if (key === '191dce0fc3fc05ea96e108e0b21a32d44d2ef04803b30ef045168da3fe8da2d1') {
    pending.push(import('./chunks/chunk-f1a5cb11d28f33e9a471794f8c925273755c7cd7c17689cdcbaca52886ef4f8f.js'));
  }
  if (key === '2e5fc8eda337aa894aeb68f9475327896ee37cf69a4ed837fe866b5df9bf5a91') {
    pending.push(import('./chunks/chunk-709d3dde7f97c0ce98b91bc51b9d436c56fa0d22835af0c0eed7b13ea50c7b80.js'));
  }
  if (key === '5df4c9c8a4e641e8eea30ac2eff9e1b16d8d4655d5cd02ecd2984a37e85703f9') {
    pending.push(import('./chunks/chunk-5d4c4ec8c90e5c2e8517b7abffc5c2086d863fe753604cc1d1f5114ebd3a8f15.js'));
  }
  if (key === 'a529da4313c7372b3f7bd306575f3b0557c6fc37e07c45d5067e7c60ca128302') {
    pending.push(import('./chunks/chunk-4906b3d66d187c79f882011efe600ac405895d8a0ca4401010a4d49c3223ec6c.js'));
  }
  if (key === 'bf0367d734ed395824167c8d6ca2278f751e24696809b26f20469122643be168') {
    pending.push(import('./chunks/chunk-247039d9abbd77cf63faf01de2b1adcaea9042328050d1dc043d2afe86a18628.js'));
  }
  if (key === '57f12d0c8ce8ebb299ee44042e28aaddeab9dce448465f6fbe13c7ad5d218ec7') {
    pending.push(import('./chunks/chunk-2c7410c21ae30f6a58304b2fe3575fb4c1686efeb4ecfdcc68691c7acd3c10a7.js'));
  }
  if (key === 'cc3de6789692e64e02937bb2aeb7a917c034a4537f4d9ed0e78b2412fdddc325') {
    pending.push(import('./chunks/chunk-f1a5cb11d28f33e9a471794f8c925273755c7cd7c17689cdcbaca52886ef4f8f.js'));
  }
  if (key === 'e7517b35115544d985dc9347ac71f0232a6d1b43474ed14d43e478b2e7e0cc4e') {
    pending.push(import('./chunks/chunk-2c7410c21ae30f6a58304b2fe3575fb4c1686efeb4ecfdcc68691c7acd3c10a7.js'));
  }
  if (key === '56e661f3fe087adf78b57e2b269dedafc43ecbce548813ee099c679c71f7ae22') {
    pending.push(import('./chunks/chunk-45bdc7b9231d2615a54b8a56bae571b8f4c5a323c06a767fd71de118064e82ed.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}