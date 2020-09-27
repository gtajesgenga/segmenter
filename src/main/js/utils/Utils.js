import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Button from "react-bootstrap/Button";
import Modal from 'react-bootstrap/Modal'

export function CustomModal (props) {
    const [show, setShow] = useState(false);
    const {mode, btnIcon, content, customClass, title, btnLabel, variant} = props;

    const icon = btnIcon !== undefined ? <FontAwesomeIcon icon={btnIcon} size={'sm'}/> : <></>;

    const cancelButton = mode === 'delete' ? <Button className={"mr-1"} variant={"light"} onClick={() => { setShow(false)}}>No</Button> : <></>;

    return (
        <>
            <div className={customClass}>
                <Button className={'float-right'} variant={variant} size={'sm'} onClick={() => setShow(true)}>
                    {icon}&nbsp;{btnLabel}
                </Button>

                <Modal show={show} onHide={() => setShow(false)}>
                    <Modal.Header closeButton>
                        <Modal.Title>{title}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {content}
                    </Modal.Body>
                    <Modal.Footer>
                        {cancelButton}
                        <Button variant={'primary'} onClick={(e) => { setShow(false); props.callback(e) }}>{btnLabel}</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        </>
    )
}
