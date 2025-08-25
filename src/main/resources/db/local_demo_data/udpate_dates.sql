DO $$
    DECLARE
        -- Identify the demo staff who created these rows (adjust emails if needed)
        v_staff_ids uuid[];

        -- Destination window
        v_new_start timestamptz := now();
        v_new_end   timestamptz := now() + interval '2 months';

        -- Source (original) window we detect from the inserted data
        v_src_min   timestamptz;
        v_src_max   timestamptz;

        v_src_span_seconds double precision;
        v_dst_span_seconds double precision;
        v_scale            double precision;
    BEGIN
        -- Collect staff IDs used as created_by in your demo data
        SELECT array_agg(id)
        INTO v_staff_ids
        FROM staff
        WHERE email IN ('reception@clinic.sa','doctor1@clinic.sa','doctor2@clinic.sa','doctor3@clinic.sa');

        -- Compute the min and max timestamps across your demo operational dates
        WITH src AS (
            SELECT min(appointment_datetime) AS min_ts, max(appointment_datetime) AS max_ts
            FROM appointments WHERE created_by = ANY (v_staff_ids)
            UNION ALL
            SELECT min(issue_date)::timestamptz, max(due_date)::timestamptz
            FROM invoices WHERE created_by = ANY (v_staff_ids)
            UNION ALL
            SELECT min(payment_date)::timestamptz, max(payment_date)::timestamptz
            FROM payments WHERE created_by = ANY (v_staff_ids)
        )
        SELECT min(min_ts), max(max_ts) INTO v_src_min, v_src_max FROM src;

        IF v_src_min IS NULL OR v_src_max IS NULL OR v_src_min >= v_src_max THEN
            RAISE NOTICE 'Skipping time remap (no source data or zero span).';
            RETURN;
        END IF;

        v_src_span_seconds := EXTRACT(EPOCH FROM (v_src_max - v_src_min));
        v_dst_span_seconds := EXTRACT(EPOCH FROM (v_new_end - v_new_start));
        v_scale := v_dst_span_seconds / v_src_span_seconds;

        -- 1) Appointments: rescale into the window
        UPDATE appointments a
        SET appointment_datetime = v_new_start + ((a.appointment_datetime - v_src_min) * v_scale)
        WHERE a.created_by = ANY (v_staff_ids);

        -- 2) Treatments: align treatment_date to the (new) appointment date
        UPDATE treatments t
        SET treatment_date = (v_new_start + ((a.appointment_datetime - v_src_min) * v_scale))::date
        FROM appointments a
        WHERE t.appointment_id = a.id
          AND a.created_by = ANY (v_staff_ids);

        -- 3) Invoices: rescale issue_date; keep original gap to due_date
        UPDATE invoices i
        SET issue_date = (v_new_start + ((i.issue_date::timestamptz - v_src_min) * v_scale))::date,
            due_date   = (v_new_start + ((i.issue_date::timestamptz - v_src_min) * v_scale))::date
                + (i.due_date - i.issue_date)
        WHERE i.created_by = ANY (v_staff_ids);

        -- 4) Payments: rescale payment_date
        UPDATE payments p
        SET payment_date = (v_new_start + ((p.payment_date::timestamptz - v_src_min) * v_scale))::date
        WHERE p.created_by = ANY (v_staff_ids);

        RAISE NOTICE 'Time remap done: % -> %  (scale=%)', v_src_min, v_src_max, v_scale;
    END
$$ LANGUAGE plpgsql;
